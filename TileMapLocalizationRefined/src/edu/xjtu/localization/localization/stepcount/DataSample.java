/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.xjtu.localization.localization.stepcount;

/**
 * 
 * @author jiangzp
 */
class DataSample {

	// double x, y, z,norm;
	double norm;
	double time;
	boolean isValid;

	// private static int count = 0;
	DataSample(double x, double y, double z, double time) {
		this(Math.sqrt(x * x + y * y + z * z), time);
	}

	public DataSample(double norm, double time) {
		// Log.i("DataSample","Building DataSample:" + count++);
		this.norm = norm;
		this.time = time;
		this.isValid = false;
	}

	public DataSample() {
		this(0, 0);
	}

	public double getNorm() {
		return norm;
	}

	public void setNorm(double norm) {
		this.norm = norm;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

}
