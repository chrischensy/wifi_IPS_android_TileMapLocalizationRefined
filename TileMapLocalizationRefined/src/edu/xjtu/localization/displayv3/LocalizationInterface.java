package edu.xjtu.localization.displayv3;

import android.webkit.JavascriptInterface;
import edu.xjtu.localization.localization.utils.Point2D;

public class LocalizationInterface {

	private V3MapDisplayActivity activity;
	private Point2D position = new Point2D();

	public void setPosition(Point2D position) {
		this.position = position;
	}

	public LocalizationInterface(V3MapDisplayActivity activity) {
		this.activity = activity;
	}

	@JavascriptInterface
	public double getX() {

		return position.x;

	}

	@JavascriptInterface
	public double getY() {

		return position.y;

	}

	@JavascriptInterface
	public void setZoomInEnable() {

		activity.runOnUiThread(new Runnable() {

			public void run() {

				if (V3MapDisplayActivity.zoomControlsButton != null) {
					V3MapDisplayActivity.zoomControlsButton
							.setIsZoomInEnabled(true);

				}
			}
		});

	}

	@JavascriptInterface
	public void setZoomOutEnable() {

		activity.runOnUiThread(new Runnable() {

			public void run() {

				if (V3MapDisplayActivity.zoomControlsButton != null) {
					V3MapDisplayActivity.zoomControlsButton
							.setIsZoomOutEnabled(true);
				}
			}
		});

	}

	@JavascriptInterface
	public void setZoomOutDisable() {

		activity.runOnUiThread(new Runnable() {

			public void run() {

				if (V3MapDisplayActivity.zoomControlsButton != null) {
					V3MapDisplayActivity.zoomControlsButton
							.setIsZoomOutEnabled(false);
				}
			}
		});

	}

	@JavascriptInterface
	public void setZoomInDisable() {

		activity.runOnUiThread(new Runnable() {

			public void run() {

				if (V3MapDisplayActivity.zoomControlsButton != null) {
					V3MapDisplayActivity.zoomControlsButton
							.setIsZoomInEnabled(false);
				}
			}
		});

	}
}
