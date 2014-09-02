package edu.xjtu.localization.localization.particlefilter;

import edu.xjtu.localization.localization.utils.TimeMessageInterface;

/**
 * 
 * @author chensy
 */
public class StepMessage implements TimeMessageInterface {
	long timeStample;
	double orientation;

	public StepMessage(long timeStample, double orientation) {
		this.timeStample = timeStample;
		this.orientation = orientation;
	}

	public long getTime() {
		return timeStample;
	}

	public void setTime(long time) {
		this.timeStample = time;
	}

	public double getOrientation() {
		return orientation;
	}

	public void setOrientation(double orientation) {
		this.orientation = orientation;
	}

	@Override
	public long getNanoTime() {
		return timeStample;
	}

}
