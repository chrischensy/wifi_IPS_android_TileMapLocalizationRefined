package edu.xjtu.localization.displayv3;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ZoomControls;
import edu.xjtu.localization.R;
import edu.xjtu.localization.localization.particlefilter.LocationUpdateTimerTask;
import edu.xjtu.localization.localization.stepcount.SensorReader;
import edu.xjtu.localization.localization.utils.Point2D;
import edu.xjtu.localization.localization.wifiservice.WifiService;

public class V3MapDisplayActivity extends Activity implements
		OnItemSelectedListener {

	private WebView webView;
	private Spinner levelSpinner;
	private Spinner methodologySpinner;
	private ImageButton myLocationButton;
	public static ZoomControls zoomControlsButton;
	private LocalizationInterface displayInterface = new LocalizationInterface(
			this);
	private static final String MAL_URL = "file:///android_asset/index.html";
	public static int INTERESTED_MESSAGE_TYPE = 1;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == INTERESTED_MESSAGE_TYPE) {
				Point2D p = (Point2D) msg.obj;

				if (Double.isNaN(p.x) || Double.isNaN(p.y)) {
					Toast.makeText(getApplicationContext(), "正在重新定位...",
							Toast.LENGTH_SHORT).show();
					LocationUpdateTimerTask.restart();
					WifiService.restart();
					return;
				}

				WifiService.setSearchCenter(p.x, p.y);
				if (msg.what == 3 || msg.what == 2) {
					LocationUpdateTimerTask.particleFilter.setCurrentPoint(p);
					LocationUpdateTimerTask.particleFilter.setSensorPoint(p);
					LocationUpdateTimerTask.particleFilter.seedParticles();
				}
				displayInterface.setPosition(p);
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		makeFullScreen();
		setContentView(R.layout.activity_map);
		myLocationButton = (ImageButton) findViewById(R.id.myLocationButton);
		levelSpinner = (Spinner) findViewById(R.id.LevelSpinner);
		methodologySpinner = (Spinner) findViewById(R.id.methodologySpinner);
		levelSpinner.setOnItemSelectedListener(this);
		methodologySpinner.setOnItemSelectedListener(this);
		setupZoomButton();
		setupMapView();
		this.startService(new Intent(this, WifiService.class));
		WifiService.setmHandler(mHandler);
		setupSensor();
	}

	@SuppressWarnings("deprecation")
	private void setupSensor() {
		// start sensor listener
		LocationUpdateTimerTask pointCalculateTimerTask = new LocationUpdateTimerTask(
				mHandler);
		Timer timer = new Timer("update", true);
		timer.schedule(pointCalculateTimerTask, 2000, 100);

		// SensorReader.handler = this.handler_point;
		SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		List<Sensor> sensorList = new ArrayList<Sensor>();
		sensorList.addAll(sensorManager
				.getSensorList(Sensor.TYPE_ACCELEROMETER));
		sensorList.add(sensorManager.getSensorList(Sensor.TYPE_ORIENTATION)
				.get(0));

		for (Sensor s : sensorList) {
			sensorManager.registerListener(SensorReader.instance(), s,
					SensorManager.SENSOR_DELAY_FASTEST);
		}
	}

	private void setupZoomButton() {

		zoomControlsButton = (ZoomControls) findViewById(R.id.zoomControlsButton);
		zoomControlsButton.setIsZoomInEnabled(false);
		zoomControlsButton.setIsZoomOutEnabled(false);
		zoomControlsButton.setOnZoomInClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				zoomButtonListener(true);
			}
		});

		zoomControlsButton
				.setOnZoomOutClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						zoomButtonListener(false);
					}
				});
	}

	private void makeFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setupMapView() {
		webView = (WebView) findViewById(R.id.mapWebView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient());
		// webView.setWebViewClient(new WebViewClient());
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webView.addJavascriptInterface(displayInterface, "android");
		webView.loadUrl(MAL_URL);

	}

	public void myLocationButtonListener(View view) {
		webView.loadUrl(getString(R.string.javascript_call_mylocationbuttonlistener_));
	}

	public void zoomButtonListener(boolean isZoomIn) {
		if (isZoomIn) {
			webView.loadUrl(getString(R.string.javascript_call_zoominbuttonlistener_));
			zoomControlsButton.setIsZoomInEnabled(false);
		} else {
			webView.loadUrl(getString(R.string.javascript_call_zoomoutbuttonlistener_));
			zoomControlsButton.setIsZoomOutEnabled(false);
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {

		if (arg0.getId() == R.id.LevelSpinner) {
			webView.loadUrl("javascript:changeMapSpinnerListener(" + pos + ")");
			if (pos != 0) {
				myLocationButton.setEnabled(false);
			} else {
				myLocationButton.setEnabled(true);
			}
		}

		if (arg0.getId() == R.id.methodologySpinner) {
			if (pos >= 0) {
				INTERESTED_MESSAGE_TYPE = pos + 1;
				Log.i("methodology", "" + INTERESTED_MESSAGE_TYPE);
			}

		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

}
