package edu.xjtu.localization.localization.wifiservice;

public interface LocationEstimater {
	void estimatePosition(double[] sampleSignals);
}
