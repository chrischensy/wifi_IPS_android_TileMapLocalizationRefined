package edu.xjtu.localization.localization.wifiservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import edu.xjtu.localization.R;
import edu.xjtu.localization.displayv3.V3MapDisplayActivity;
import edu.xjtu.localization.localization.utils.MessageBufferManager;
import edu.xjtu.localization.localization.utils.Point2D;

public class WifiService extends Service implements LocationEstimater {
	private static double currentX = 0, currentY = 0;
	
	public static double searchRange = 99;
	private final static int MAX_DISTANCE_INIT = 5;
	public final static double RSSI_MIN_LEVEL = -93.0;
	private static boolean initializeCompleted = false;
	private List<Point2D> offsetList = new ArrayList<Point2D>();

	private List<HistoryInfo> historyInfo;
	private WifiManager wifiManager;
	private static Handler mHandler;

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	public static void restart() {
		Log.i("WIFI_RESTART", "restart");
		currentX = 0;
		currentY = 0;
		searchRange = 8;
		WifiReceiver.expectedScanTimes = 4;
		initializeCompleted = false;
	}

	public static Handler getmHandler() {
		return mHandler;
	}

	public static void setmHandler(Handler mHandler1) {
		mHandler = mHandler1;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// Log.i("WIFI_SERVICE", "onCreate.....");

		// Wifi initialization
		historyInfo = new ArrayList<HistoryInfo>();
		loadBaseData();
		wifiManager = (WifiManager) getSystemService("wifi");
		if (!wifiManager.isWifiEnabled())
			if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING)
				wifiManager.setWifiEnabled(true);
		WifiReceiver wifiReceiver = new WifiReceiver(wifiManager, this);
		registerReceiver(wifiReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		wifiManager.startScan();

	}

	private void loadBaseData() {
		InputStream is = getResources().openRawResource(R.raw.data3);
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String line_read = null;
			HistoryInfo tmp = new HistoryInfo();
			int readcase = ReadCase.SIGS_READ;
			while ((line_read = br.readLine()) != null) {

				String line = line_read;
				String[] s = line.split(",");
				switch (readcase) {
				case ReadCase.SIGS_READ:
					for (int j = 0; j < s.length; j++) {
						tmp.sigs.add(Double.valueOf(s[j]).doubleValue());
					}
					readcase = ReadCase.POINTX_READ;
					break;

				case ReadCase.POINTX_READ:
					for (int j = 0; j < s.length; j++) {
						tmp.points.add(new Point2D(Double.valueOf(s[j])
								.doubleValue(), 0));
					}
					readcase = ReadCase.POINTY_READ;
					break;

				case ReadCase.POINTY_READ:
					for (int j = 0; j < s.length; j++) {
						tmp.points.get(j).setY(
								Double.valueOf(s[j]).doubleValue());
					}
					historyInfo.add(tmp);
					tmp = new HistoryInfo();
					;
					readcase = ReadCase.SIGS_READ;
					break;
				}
			}
			// for(HistoryInfo tmp1:historyInfo){
			// Log.i("load_tmp", String.format(
			// "sig:%d %f %f-x:%d %f %f-y:%f %f", tmp1.sigs.size(),
			// tmp1.sigs.get(1), tmp1.sigs.get(tmp1.sigs.size() - 1),
			// tmp1.points.size(), tmp1.points.get(1).getX(),
			// tmp1.points.get(tmp1.sigs.size() - 1).getX(),
			// tmp1.points.get(1).getY(),
			// tmp1.points.get(tmp1.sigs.size() - 1).getY()));
			// }
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.i("WIFI_SERVICE", "Finish Read History Data.....");
	}

	public static void setSearchCenter(double x, double y) {
		currentX = x;
		currentY = y;
	}

	public void estimatePosition(double[] sampleSignals) {
		int MAX_DISTANCE = MAX_DISTANCE_INIT;
		while (true) {
			boolean mark = false;
			for (int i = 0; i < sampleSignals.length; i++) {
				//Log.i("calculateAps", String.format("%d...%f...done", i,sampleSignals[i]));
				if (sampleSignals[i] == RSSI_MIN_LEVEL)
					continue;
				List<Point2D> offsetListtmp;
				offsetListtmp = historyInfo.get(i).getPossiblePoints(
						sampleSignals[i], MAX_DISTANCE);
				if (offsetListtmp.size() == 0)
					continue;
				//Log.i("offsetListtmpsize", String.format("%d", offsetListtmp.size()));
				for (int j =0;j<offsetListtmp.size();j++) {
					if (!offsetListtmp.get(j).IsInArea(currentX, currentY, searchRange)) {
						offsetListtmp.remove(j);
						j--;
					}
				}
				if (offsetListtmp.size() == 0)
					continue;
				if (!mark) {
					offsetList = offsetListtmp;
					mark = true;
				} else {
					for (int j =0;j<offsetList.size();j++) {
						boolean deletemark = true;
						for (Point2D offsettmp : offsetListtmp) {
							if (offsetList.get(j).equals(offsettmp)) {
								deletemark = false;
								break;
							}
						}
						if (deletemark) {
							offsetList.remove(j);
							j--;
						}
					}
				}
				//Log.i("offsetListsize", String.format("%d", offsetList.size()));
			}
			if (offsetList.size() != 0) {
				Log.i("offsetList.size()",String.format("%d", offsetList.size()));
				break;
			} else {
				MAX_DISTANCE++;
			}
			Log.i("MAX_DISTANCE", String.format("%d", MAX_DISTANCE));
			//break;
		}
		long time_long = System.nanoTime();
		Point2D p = null;
		if (!initializeCompleted) {
			searchRange = 99;
			p = calculateCenter();
			initializeCompleted = true;
		} else {
			p = calculateCenter();
		}

		Message msg = new Message();
		msg.obj = p;
		if (V3MapDisplayActivity.INTERESTED_MESSAGE_TYPE == 3) {
			msg.what = 3;// wifi position;
			mHandler.sendMessage(msg);
		} else if (V3MapDisplayActivity.INTERESTED_MESSAGE_TYPE == 2) {
			msg.what = 2;// wifi position;
			mHandler.sendMessage(msg);
		}

		// Log.i("WIFI_ESTIMATE",p.toString());
		MessageBufferManager.addWifiMessage(new WifiMessage(time_long, p));
		offsetList.clear();
		offsetList = new ArrayList<Point2D>();
	}

	/**
	 * calculate the estimated position based on the candidates in offsetList
	 * 
	 * @return
	 */
	private Point2D calculateCenter() {
		Point2D center = new Point2D(0, 0);
		int count = 0;
		for (Point2D d : offsetList) {
			center.x += d.getX();
			center.y += d.getY();
			count++;
		}
		center.x = center.x / count;
		center.y = center.y / count;
		Log.i("position", String.format("%f--%f", center.x,center.y));
		return center;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("WIFI_SERVICE", "on destroy");
	}

}
