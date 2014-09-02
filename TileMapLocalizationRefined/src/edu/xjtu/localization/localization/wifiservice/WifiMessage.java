package edu.xjtu.localization.localization.wifiservice;

import edu.xjtu.localization.localization.utils.Point2D;
import edu.xjtu.localization.localization.utils.TimeMessageInterface;

public class WifiMessage implements TimeMessageInterface {
	private long timeStample;
	private Point2D point = null;

	public WifiMessage(long timeStample, Point2D point) {
		super();
		this.timeStample = timeStample;
		this.point = point;
	}

	public long getTimeStample() {
		return timeStample;
	}

	public void setTimeStample(long timeStample) {
		this.timeStample = timeStample;
	}

	public Point2D getPoint() {
		return point;
	}

	public void setPoint(Point2D point) {
		this.point = point;
	}

	@Override
	public long getNanoTime() {

		return timeStample;
	}

}
