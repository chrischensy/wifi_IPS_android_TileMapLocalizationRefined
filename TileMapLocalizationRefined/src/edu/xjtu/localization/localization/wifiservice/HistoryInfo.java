package edu.xjtu.localization.localization.wifiservice;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import edu.xjtu.localization.localization.utils.Point2D;

public class HistoryInfo {
	public List<Point2D> points;
	public List<Double> sigs;

	public HistoryInfo() {
		points = new ArrayList<Point2D>();
		sigs = new ArrayList<Double>();
	};

	public List<Point2D> getPossiblePoints(double rssi, double MAX_DISTANCE) {
		//Log.i("getRssiandHistoryInfosize", String.format("%f...%d...%d",rssi,this.sigs.size(),this.points.size()));
		int lowestLimitation = getLowestLimitation(rssi, MAX_DISTANCE);
		int highestLimitation;
		if (lowestLimitation >= sigs.size()) {
			return new ArrayList<Point2D>();
		} else {
			highestLimitation = getHighestLimitation(rssi, MAX_DISTANCE,
					lowestLimitation + 1);
			//Log.i("getPossiblePoints",String.format("...%d...%d",lowestLimitation,highestLimitation));
			return getPoints(lowestLimitation, highestLimitation);
		}
	}

	private List<Point2D> getPoints(int lowestLimitation, int highestLimitation) {
		List<Point2D> points = new ArrayList<Point2D>();
		for (int i = lowestLimitation; i < highestLimitation; i++) {
			points.add(new Point2D(this.points.get(i)));
		}
		return points;
	}

	private int getLowestLimitation(double rssi, double MAX_DISTANCE) {
		double rssiLowLevel = rssi - MAX_DISTANCE;
		int i;
		for (i = 0; i < sigs.size(); i++) {
			if (rssiLowLevel <= sigs.get(i)) {
				break;
			}
		}
		return i;
	}

	private int getHighestLimitation(double rssi, double MAX_DISTANCE,
			int lowestLimitation) {
		if (lowestLimitation >= sigs.size())
			return sigs.size();
		double rssiHighLevel = rssi + MAX_DISTANCE;
		int i;
		for (i = lowestLimitation; i < sigs.size(); i++) {
			if (rssiHighLevel < sigs.get(i)) {
				break;
			}
		}
		return i;
	}
}
