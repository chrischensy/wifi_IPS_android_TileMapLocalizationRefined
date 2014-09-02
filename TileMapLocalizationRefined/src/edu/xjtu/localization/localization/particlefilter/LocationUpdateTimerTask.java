package edu.xjtu.localization.localization.particlefilter;

import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.xjtu.localization.displayv3.V3MapDisplayActivity;
import edu.xjtu.localization.localization.utils.MessageBufferManager;
import edu.xjtu.localization.localization.utils.Point2D;
import edu.xjtu.localization.localization.wifiservice.WifiMessage;
import edu.xjtu.localization.localization.wifiservice.WifiService;

/**
 * 
 * @author chensy
 */
public class LocationUpdateTimerTask extends TimerTask {
	public static ParticleFilter particleFilter;

	private Handler mHandler;
	private static boolean initializationCompleted = false;

	public static void restart() {
		Log.i("LocationService", "restart");
		initializationCompleted = false;
	}

	public LocationUpdateTimerTask(Handler mHandler) {

		particleFilter = new ParticleFilter();
		this.mHandler = mHandler;
	}

	@Override
	public void run() {
		if (!initializationCompleted) {
			WifiMessage w = MessageBufferManager.drainWifiMessage();
			if (w == null) {
				return;
			} else {
				Log.i("WIFI_FIRST", w.getPoint().toString());
				initializationCompleted = true;
				particleFilter.setCurrentPoint(w.getPoint());
				particleFilter.setSensorPoint(w.getPoint());
				particleFilter.seedParticles();
			}
		} else {
			StepMessage s = MessageBufferManager.drainStepMessage();
			if (s == null) {
				return;
			}

			WifiMessage w = MessageBufferManager.drainWifiMessage();
			if (w != null) {
				particleFilter.setWifiPoint(w.getPoint());
			}

			particleFilter.calculate(s.getOrientation());
		}

		if (V3MapDisplayActivity.INTERESTED_MESSAGE_TYPE == 1) {
			Point2D p = particleFilter.getCurrentPoint();
			Message msg = new Message();
			msg.obj = p;
			msg.what = 1;// total position;
			mHandler.sendMessage(msg);
		} else if (V3MapDisplayActivity.INTERESTED_MESSAGE_TYPE == 4) {
			Point2D p = particleFilter.getSensorPoint();
			Message msg2 = new Message();
			msg2.obj = p;
			msg2.what = 4;// sensor position;
			mHandler.sendMessage(msg2);
		}

		if (V3MapDisplayActivity.INTERESTED_MESSAGE_TYPE == 1) {
			ParticleFilter.alpha = 0.8;
		}

		if (V3MapDisplayActivity.INTERESTED_MESSAGE_TYPE == 4) {
			ParticleFilter.alpha = 1;
		}

		if (V3MapDisplayActivity.INTERESTED_MESSAGE_TYPE == 3) {
			ParticleFilter.alpha = 1;
			WifiService.searchRange = 99;
		} else {
			WifiService.searchRange = 8;
		}

	}

}
