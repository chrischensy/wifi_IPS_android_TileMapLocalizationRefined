package edu.xjtu.localization.localization.wifiservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiReceiver extends BroadcastReceiver {

	public static int expectedScanTimes = 10;
	private boolean initCompleted = false;
	private static int scanCount = 0;
	private final static double RSSI_MIN_LEVEL = -93.0;
	private static List<String> macs = new ArrayList<String>();

	// private final static String macs[] = { "c6:c6:c6:c6:c6:16",
	// "c6:c6:c6:c6:c6:14", "74:d0:2b:89:83:88", "c6:c6:c6:c6:c6:07",
	// "c6:c6:c6:c6:c6:06", "ec:88:8f:c7:07:e6", "c6:c6:c6:c6:c6:01",
	// "6c:e8:73:9b:45:96", "d0:c7:c0:71:74:54", "d0:c7:c0:71:5b:76",
	// "6c:6c:6c:6c:6c:09", "c6:c6:c6:c6:c6:13", "c6:c6:c6:c6:c6:20",
	// "c6:c6:c6:c6:c6:11", "00:90:4c:60:00:1c", "c6:c6:c6:c6:c6:05" };

	private List<ArrayList<Integer>> signals = new ArrayList<ArrayList<Integer>>();
	private WifiManager wifiManager;
	private LocationEstimater estimater;
	{
		macs.add("c6:c6:c6:c6:c6:16");
		macs.add("c6:c6:c6:c6:c6:14");
		macs.add("74:d0:2b:89:83:88");
		macs.add("00:21:6a:bd:77:58");
		macs.add("c6:c6:c6:c6:c6:06");
		macs.add("ec:88:8f:c7:07:e6");
		macs.add("c6:c6:c6:c6:c6:01");
		macs.add("d0:c7:c0:71:74:54");
		macs.add("d0:c7:c0:71:5b:76");
		macs.add("6c:6c:6c:6c:6c:09");
		macs.add("c6:c6:c6:c6:c6:13");
		macs.add("c6:c6:c6:c6:c6:20");
		macs.add("c6:c6:c6:c6:c6:11");
		macs.add("00:90:4c:60:00:1c");
		macs.add("c6:c6:c6:c6:c6:05");
	}

	public WifiReceiver(WifiManager wifiManager, LocationEstimater estimater) {
		super();
		this.wifiManager = wifiManager;
		this.estimater = estimater;
		for (int i = 0; i < macs.size(); i++) {
			signals.add(new ArrayList<Integer>());
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		scanCount++;
		List<ScanResult> results = wifiManager.getScanResults();

		if (scanCount <= expectedScanTimes) {

			for (ScanResult result : results) {
				int index = find(result.BSSID);
				if (index == -1) {
					continue;
				}
				signals.get(index).add(result.level);
			}
		} else {

			double[] sampleSignals = new double[macs.size()];
			for (int i = 0; i < signals.size(); i++) {
				List<Integer> tmp = signals.get(i);
				Collections.sort(tmp);
				if (tmp.size() >= expectedScanTimes / 2) {
					if (tmp.size() % 2 == 1) {
						sampleSignals[i] = tmp.get(tmp.size() / 2);
					} else {
						sampleSignals[i] = (tmp.get(tmp.size() / 2) + tmp
								.get((tmp.size() - 1) / 2)) / 2.0;
					}
				} else {
					sampleSignals[i] = RSSI_MIN_LEVEL;
				}
				tmp.clear();
			}
			//double[] sampleSignalstest = {-76.0, -93.0, -58.5, -62.0, -56.0, -56.0, -54.5, -77.0, -70.0, -62.0, -66.0, -78.0, -59.5, -93.0, -65.0};
			Log.i("sampleS:", Arrays.toString(sampleSignals));
			estimater.estimatePosition(sampleSignals);
			//estimater.estimatePosition(sampleSignalstest);
			scanCount = 0;
			if (!initCompleted) {
				initCompleted = true;
				expectedScanTimes = 2;
			}
		}

		wifiManager.startScan();
	}

	private int find(String target) {
		for (int i = 0; i < macs.size(); i++) {
			if (macs.get(i).equals(target)) {
				return i;
			}
		}
		return -1;
	}

}
