package edu.xjtu.localization.localization.particlefilter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;

import android.util.Log;
import edu.xjtu.localization.localization.utils.MapRestrictor;
import edu.xjtu.localization.localization.utils.Point2D;

public class ParticleFilter {
	// particles' total count
	private static final int N = 200;
	private static final double weight = 1.0 / N;
	private static final double STEP_LENGTH_MEAN = 0.6;
	private static final double STEP_LENGTH_STD = 0.1;
	private static final double ORIENTATION_STD = 0.3;
	// final mean orientation = ORIENTATION_BIAS - orientation. 0 means south;
	// 180 means north.
	private static final double ORIENTATION_BIAS = -5;
	// scale the results from sensor signals and wifi signals.The weight of
	// sensor's result will be higher when alpha become bigger.
	public static double alpha = 0.5;

	private Point2D currentPoint;
	private Point2D sensorPoint;
	private Point2D wifiPoint = null;
	private List<Particle> particleList = null;
	private NormalDistribution stepLengthGenerator = new NormalDistribution(
			STEP_LENGTH_MEAN, STEP_LENGTH_STD);
	private NormalDistribution orientationGenerator = new NormalDistribution(0,
			ORIENTATION_STD);
	private static Point2D tmpPoint = new Point2D();
	private static final int maxAttempt = 20;
	private static double tmpOrientation = 0;
	private static double tmpStepLength = 0;

	public ParticleFilter() {
		particleList = new ArrayList<Particle>();
		for (int i = 0; i < N; i++) {
			particleList.add(new Particle(new Point2D(), weight));
		}
		setCurrentPoint(new Point2D(1, 1));
		setSensorPoint(new Point2D(1, 1));
	}

	public Point2D getSensorPoint() {
		return sensorPoint;
	}

	public void setSensorPoint(Point2D sensorPoint) {
		this.sensorPoint = sensorPoint;
	}

	public Point2D getCurrentPoint() {
		return currentPoint;
	}

	public Point2D getWifiPoint() {
		return wifiPoint;
	}

	public void setWifiPoint(Point2D wifiPoint) {
		this.wifiPoint = wifiPoint;
	}

	public void setCurrentPoint(Point2D currentPoint) {
		this.currentPoint = currentPoint;
	}

	public Point2D calculate(double orientation) {
		orientation = ORIENTATION_BIAS - orientation;
		orientation = orientation * Math.PI / 180;

		// update particles' position based on step event
		updateParticles(orientation);
		// calculate the center of particles, which could be center of mass or
		// center of figure
		currentPoint = particlesCenter();
		sensorPoint.x = currentPoint.x;
		sensorPoint.y = currentPoint.y;
		killBadParticles(STEP_LENGTH_MEAN * 1);
		correctParticles(orientation);

		// revise the estimated position based on the Wi-Fi knowledge
		if (wifiPoint != null) {
			tmpPoint.x = alpha * currentPoint.x + (1 - alpha) * wifiPoint.x;
			tmpPoint.y = alpha * currentPoint.y + (1 - alpha) * wifiPoint.y;
			if (MapRestrictor.inAnyRect(tmpPoint) == false) {
				currentPoint.x = tmpPoint.x;
				currentPoint.y = tmpPoint.y;
			}
			seedParticles();
			wifiPoint = null;
		}

		Log.i("ParticleFilter.Location", currentPoint.toString());
		return currentPoint;

	}

	private void killBadParticles(double range) {
		Point2D point;
		double diffX = 0;
		double diffY = 0;
		for (Particle p : particleList) {
			point = p.getPoint();
			diffX = Math.abs(currentPoint.x - point.x);
			diffY = Math.abs(currentPoint.y - point.y);
			if (diffX * diffX + diffY * diffY > range) {
				p.setWeight(0);
			}
		}
	}

	// update particles' location based on the direction and stepLength, which
	// are subjected to normal distribution
	private void updateParticles(double orientation) {
		tmpStepLength = 0;
		tmpOrientation = 0;
		for (Particle p : particleList) {
			tmpStepLength = stepLengthGenerator.sample();
			tmpOrientation = orientationGenerator.sample() + orientation;
			Point2D point = p.getPoint();
			tmpPoint.x = point.x + tmpStepLength * Math.cos(tmpOrientation);
			tmpPoint.y = point.y + tmpStepLength * Math.sin(tmpOrientation);
			if (MapRestrictor.judgeMovement(point, tmpPoint)) {
				point.x = tmpPoint.x;
				point.y = tmpPoint.y;
			} else {
				p.setWeight(0);
			}
		}

	}

	private Point2D particlesCenter() {
		// calculate the center of the particles.

		// the center of mass
		double x = 0.0;
		double y = 0.0;
		double cumulateWeight = 0;
		for (Particle p : particleList) {
			cumulateWeight += p.getWeight();
			x += p.getPoint().x * p.getWeight();
			y += p.getPoint().y * p.getWeight();
		}
		x = x / cumulateWeight;
		y = y / cumulateWeight;
		return new Point2D(x, y);
	}

	public void resampleParticle(Particle p) {
		// sample one particle in an circle area whose center is (x, y) and
		// radius is indicated as 'range'.
		double range = STEP_LENGTH_MEAN;
		while (true) {
			tmpPoint.x = (Math.random() - 0.5) * 2 * range;
			tmpPoint.y = (Math.random() - 0.5) * 2 * range;

			if (tmpPoint.x * tmpPoint.x + tmpPoint.y * tmpPoint.y < range
					* range) {
				tmpPoint.x += currentPoint.x;
				tmpPoint.y += currentPoint.y;
				if (MapRestrictor.judgeMovement(currentPoint, tmpPoint)) {
					p.getPoint().x = tmpPoint.x;
					p.getPoint().y = tmpPoint.y;
					p.setWeight(weight);
					break;
				}
			}
		}
	}

	public void resampleParticle(Particle p, double orientation) {
		tmpStepLength = 0;
		tmpOrientation = 0;
		Point2D point = p.getPoint();

		for (int i = 0; i < maxAttempt; i++) {
			tmpStepLength = stepLengthGenerator.sample();
			tmpOrientation = orientationGenerator.sample() + orientation;
			tmpPoint.x = point.x + tmpStepLength * Math.cos(tmpOrientation);
			tmpPoint.y = point.y + tmpStepLength * Math.sin(tmpOrientation);
			if (MapRestrictor.judgeMovement(point, tmpPoint)) {
				point.x = tmpPoint.x;
				point.y = tmpPoint.y;
				p.setWeight(weight);
				break;
			}
		}
	}

	public void seedParticles() {
		for (Particle p : particleList) {
			this.resampleParticle(p);
		}
	}

	private void correctParticles(double orientation) {
		for (Particle p : particleList) {
			if (p.getWeight() == 0) {
				resampleParticle(p, orientation);
			}
		}
	}
}
