package edu.xjtu.localization.localization.stepcount;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import edu.xjtu.localization.localization.particlefilter.StepMessage;
import edu.xjtu.localization.localization.utils.MessageBufferManager;

/**
 * 
 * @author jiangzp & chensy refined by chengp
 */
public class SensorReader implements SensorEventListener {
	// use constraints to acc data retrieving.
	private static final int TYPE_ACC = Sensor.TYPE_ACCELEROMETER;
	@SuppressWarnings("deprecation")
	private static final int TYPE_ORI = Sensor.TYPE_ORIENTATION;
	private static int DETECTEDLAG_MIN = 150;
	private static int DETECTEDLAG_RESET = 220;

	private static int LAG_MARGIN = 40;
	private static int IDLE = 0;
	private static int WALKING = 1;
	private static int currentState = IDLE;
	private static double IDEL_STATE_THRESHOLD = 0.125;
	private static double WALKING_STATE_THRESHOLD = 0.55;
	private static int firstHopeMaxLag = 200;
	private static int maxLag_firsthope = firstHopeMaxLag - 10;
	private static int minLag = 200;
	private static int maxLag = 400;
	private static int currentIndex;
	private static PoolableObjectFactory<DataSample> dataSamplePoolFactory = new DataSamplePoolFactory();
	private static GenericObjectPool<DataSample> dataSamplePool = new GenericObjectPool<DataSample>(
			dataSamplePoolFactory);

	private static List<DataSample> originalDataList = new ArrayList<DataSample>();
	private static List<DataSample> orientationDataList = new ArrayList<DataSample>();
	private static List<DataSample> resampledDataSampleList = new ArrayList<DataSample>();
	private static int targetHertz = 200; // the target frequency for resampling
	private static DescriptiveStatistics descriptiveStatistics;
	public static int totalStep = 0;
	private static long currentNanoTime = 0;

	private void AddDataSampleToPool() {
		try {
			dataSamplePool.addObject();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private DataSample borrowDataSample() {
		if (dataSamplePool.getNumIdle() <= 0) {
			AddDataSampleToPool();
		}
		DataSample d = null;
		try {
			d = dataSamplePool.borrowObject();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}

	private void returnDataSample(DataSample d) {
		try {
			dataSamplePool.returnObject(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clearDataSampleList(List<DataSample> list) {
		for (DataSample d : list) {
			returnDataSample(d);
		}
		list.clear();
	}

	/**
	 * Singleton
	 */
	private static SensorReader instance = new SensorReader();

	private SensorReader() {
		dataSamplePool.setMaxActive(Integer.MAX_VALUE);
		dataSamplePool.setMaxIdle(Integer.MAX_VALUE);
	}

	public static SensorEventListener instance() {
		return instance;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public void onSensorChanged(SensorEvent event) {
		long time = event.timestamp;
		double x = event.values[0];
		double y = event.values[1];
		double z = event.values[2];

		if (event.sensor.getType() == TYPE_ORI) {

			DataSample orientationData = borrowDataSample();
			orientationData.setNorm(x);
			orientationData.setTime(time);
			orientationDataList.add(orientationData);
		}
		if (event.sensor.getType() == TYPE_ACC) {
			if (currentNanoTime >= time) {
				return;
			}
			currentNanoTime = time;
			DataSample originalData = borrowDataSample();
			originalData.setNorm(Math.sqrt(x * x + y * y + z * z));
			originalData.setTime(time);
			originalDataList.add(originalData);

			int maxLagNow = (int) ((time - originalDataList.get(0).time)
					* targetHertz / 1e9);

			if (2 * maxLag + (firstHopeMaxLag - maxLag_firsthope) <= maxLagNow) {
				correlation();
				double cleartime = currentIndex * 1e9 / targetHertz
						+ originalDataList.get(0).time;
				int i = 0;
				for (i = 0; i < originalDataList.size(); i++) {
					if (cleartime <= originalDataList.get(i).time) {
						break;
					}
				}
				clearDataSampleList(originalDataList.subList(0, i));
			}
		}
	}

	private void correlation() {
		sensorDataResampler(targetHertz);
		totalStep += correlationBasedCount(); // cound the steps.
	}

	private void sensorDataResampler(int targetFrequency) {
		int originalDataListsize = originalDataList.size();
		double[] samples = new double[originalDataListsize];
		double[] timestamps = new double[originalDataListsize];

		for (int i = 0; i < originalDataListsize; i++) {
			samples[i] = originalDataList.get(i).norm;
			timestamps[i] = originalDataList.get(i).time;
		}
		for (int i = 1; i < originalDataListsize; i++) {
			timestamps[i] = (timestamps[i] - timestamps[0]) / 1e9;
		}
		timestamps[0] = 0;
		int resampledTimeStampsLength = (int) Math
				.floor(timestamps[timestamps.length - 1] * targetFrequency);
		UnivariateInterpolator samplesInterpolator = new SplineInterpolator();
		// Log.i("OriginalDataListsize",""+originalDataListsize);
		UnivariateFunction samplesInterpolatorFunction = samplesInterpolator
				.interpolate(timestamps, samples);
		clearDataSampleList(resampledDataSampleList);
		double timestamp = 0;
		for (int i = 0; i < resampledTimeStampsLength; i++) {
			DataSample d = borrowDataSample();
			timestamp = 1.0 / targetFrequency * i;
			d.setNorm(samplesInterpolatorFunction.value(timestamp));
			d.setTime(timestamp);
			resampledDataSampleList.add(d);
		}
		clearDataSampleList(resampledDataSampleList.subList(0,
				resampledDataSampleList.size() - 2 * maxLag));

	}

	public int correlationBasedCount() {
		currentIndex = 0;
		long time = (long) originalDataList.get(0).time;
		double ori = calculateOrientation();

		int detectedLag = (minLag + maxLag) / 2;
		double maxNAC = 0.0;

		descriptiveStatistics = new DescriptiveStatistics();

		double seqSTD;
		descriptiveStatistics.clear();
		for (int i = (int) (detectedLag * 3 / 2) - 1; i < 2 * detectedLag - 1; i++) {
			descriptiveStatistics.addValue(resampledDataSampleList.get(i).norm);
		}
		seqSTD = descriptiveStatistics.getStandardDeviation();

		if (seqSTD < IDEL_STATE_THRESHOLD) {
			currentState = IDLE;

		} else {
			double tmpList[] = findOptimalLagContinuouCalculate(minLag, maxLag);
			if (tmpList == null) {
				return 0;
			}
			detectedLag = (int) tmpList[0];
			maxNAC = tmpList[1];

			if (detectedLag < DETECTEDLAG_MIN) {
				maxLag = DETECTEDLAG_RESET + LAG_MARGIN;
				minLag = DETECTEDLAG_RESET - LAG_MARGIN;
				return 0;
			} else {
				maxLag = detectedLag + LAG_MARGIN;
				minLag = detectedLag - LAG_MARGIN;
			}

			if (maxNAC > WALKING_STATE_THRESHOLD) {
				currentState = WALKING;
			}
		}
		currentIndex = (int) Math.ceil(detectedLag / 2);

		if (currentState == WALKING) {
			MessageBufferManager.addStepMessage(new StepMessage(time, ori));
		}

		return 1;
	}

	private double calculateOrientation() {
		double ori = 0.0;
		for (int i = 0; i < orientationDataList.size(); i++) {
			ori += orientationDataList.get(i).norm;
		}
		ori = ori / orientationDataList.size();
		clearDataSampleList(orientationDataList);
		return ori;
	}

	private double[] findOptimalLagContinuouCalculate(int minLag, int maxLag) {
		if (maxLag * 2 > resampledDataSampleList.size()) {
			Log.i("Out of List", "not enough data sample.");
			return null;
		}
		double results[] = new double[2];
		double maxNormalizedAutoCorr = -1;
		int optLag = (minLag + maxLag) / 2;
		double modulusSum1 = 0;
		double modulusSum2 = 0;
		double sum1 = 0;
		double sum2 = 0;
		double tmp1 = 0;
		double tmp2 = 0;

		for (int i = 1; i <= minLag - 1; i++) {
			tmp1 = resampledDataSampleList.get(i - 1).getNorm();
			tmp2 = resampledDataSampleList.get(i - 1 + minLag - 1).getNorm();
			sum1 += tmp1;
			sum2 += tmp2;
			modulusSum1 += tmp1 * tmp1;
			modulusSum2 += tmp2 * tmp2;
		}

		double tmp3 = 0;
		double mean1 = 0;
		double mean2 = 0;
		double std1 = 0;
		double std2 = 0;
		double firstPart = 0;
		double secondPart = 0;
		double tmpAutoCorr = 0;
		for (int tmpLag = minLag; tmpLag <= maxLag; tmpLag++) {
			tmp1 = resampledDataSampleList.get(tmpLag - 1).getNorm();
			tmp2 = resampledDataSampleList.get(tmpLag * 2 - 1).getNorm();
			tmp3 = resampledDataSampleList.get(tmpLag * 2 - 2).getNorm();

			sum1 += tmp1;
			sum2 = sum2 - tmp1 + tmp2 + tmp3;
			mean1 = sum1 / tmpLag;
			mean2 = sum2 / tmpLag;

			modulusSum1 += tmp1 * tmp1;
			modulusSum2 = modulusSum2 - tmp1 * tmp1 + tmp2 * tmp2 + tmp3 * tmp3;

			std1 = Math.sqrt((modulusSum1 - tmpLag * mean1 * mean1)
					/ (tmpLag - 1));
			std2 = Math.sqrt((modulusSum2 - tmpLag * mean2 * mean2)
					/ (tmpLag - 1));

			secondPart = std1 * std2 * tmpLag;
			firstPart = 0.0;
			for (int i = 0; i < tmpLag; i++) {
				firstPart += (resampledDataSampleList.get(i).getNorm() - mean1)
						* (resampledDataSampleList.get(i + tmpLag).getNorm() - mean2);
			}
			tmpAutoCorr = firstPart / secondPart;
			if (tmpAutoCorr > maxNormalizedAutoCorr) {
				maxNormalizedAutoCorr = tmpAutoCorr;
				optLag = tmpLag;
			}
		}
		results[0] = optLag;
		results[1] = maxNormalizedAutoCorr;
		return results;
	}

}
