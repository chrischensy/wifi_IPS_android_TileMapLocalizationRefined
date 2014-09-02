package edu.xjtu.localization.displayv2;

import java.util.Timer;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;

import edu.xjtu.localization.R;
import edu.xjtu.localization.displayv2.tileprovider.XJTUQJTileProvider;
import edu.xjtu.localization.displayv2.utils.CoordinateTransformation;
import edu.xjtu.localization.localization.particlefilter.LocationUpdateTimerTask;
import edu.xjtu.localization.localization.stepcount.SensorReader;
import edu.xjtu.localization.localization.utils.Point2D;
import edu.xjtu.localization.localization.wifiservice.WifiService;

public class V2MapDisplayActivity extends FragmentActivity implements
		OnCameraChangeListener, OnMapClickListener,
		OnMyLocationButtonClickListener, OnMarkerClickListener {

	private static int MAP_MIN_ZOOM_LEVEL = 3;
	private static int MAP_MAX_ZOOM_LEVEL = 7;
	private GoogleMap googleMap;
	private Marker myLocationMarker; // centerMarker;
	private boolean isLocalizationButtonEnable = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		makeFullScreen();
		setContentView(R.layout.activity_tile_map_display);
		setupGoogleMap();
		setupService();
		Toast.makeText(getApplicationContext(), "请等待初次定位...", Toast.LENGTH_LONG).show();

	}

	private void makeFullScreen() {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	private void setupGoogleMap() {
		googleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
		googleMap.addTileOverlay(new TileOverlayOptions()
				.tileProvider(new XJTUQJTileProvider(getApplicationContext())));
		googleMap.setMyLocationEnabled(true);
		googleMap.getUiSettings().setCompassEnabled(false);
		googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		googleMap.getUiSettings().setRotateGesturesEnabled(false);
		googleMap.getUiSettings().setTiltGesturesEnabled(false);
		googleMap.getUiSettings().setZoomControlsEnabled(false);
		googleMap.setOnCameraChangeListener(this);
		googleMap.setOnMapClickListener(this);
		googleMap.setOnMarkerClickListener(this);
		googleMap.setOnMyLocationButtonClickListener(this);
		googleMap.getUiSettings().setMyLocationButtonEnabled(isLocalizationButtonEnable);
		myLocationMarker = googleMap.addMarker(new MarkerOptions().position(
				new LatLng(0, 0)).title("My Location"));
		myLocationMarker.setIcon(BitmapDescriptorFactory.defaultMarker(50));
		myLocationMarker.setVisible(isLocalizationButtonEnable);
//		centerMarker = googleMap.addMarker(new MarkerOptions().position(
//				new LatLng(0, 0)).title("center"));
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(1));
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(CoordinateTransformation.getMapCenter()));
	}

	@SuppressWarnings("deprecation")
	private void setupService() {
	
		this.startService(new Intent(this, WifiService.class));
		SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		sensorManager.registerListener(SensorReader.instance(), sensorManager
				.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),
				SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(SensorReader.instance(), sensorManager
				.getSensorList(Sensor.TYPE_ORIENTATION).get(0),
				SensorManager.SENSOR_DELAY_FASTEST);

		Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				Point2D p = (Point2D) msg.obj;
				updateLocation(p);
			}
		};
		LocationUpdateTimerTask timerTask = new LocationUpdateTimerTask(mHandler);
		Timer timer = new Timer("LocationUpdateTimer", true);
		timer.schedule(timerTask, 2000, 100);
	}

	private void updateLocation(Point2D p) {
		if (isLocalizationButtonEnable == false) {
			Toast.makeText(getApplicationContext(), "已经完成初始定位，请点击右上角定位键", Toast.LENGTH_LONG).show();
		}
		isLocalizationButtonEnable  = true;
		googleMap.getUiSettings().setMyLocationButtonEnabled(isLocalizationButtonEnable);
		myLocationMarker.setVisible(isLocalizationButtonEnable);
		
		LatLng newPosition = CoordinateTransformation.locationToLatLng(p.x,p.y);
		Log.i("LagLag Location", newPosition.toString());
		myLocationMarker.setPosition(newPosition);
	};
	
	

	/**
	 * Implements the onCameraChange methods of OnCameraChangeListener
	 * interface.
	 */
	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
		// Log.i("onCameraChange", paramCameraPosition.toString());
//		LatLng center = cameraPosition.target;
//		centerMarker.setPosition(center);
//		Log.i("centercamera",center.toString());
		
		
		if (cameraPosition.zoom >= MAP_MAX_ZOOM_LEVEL) {
			googleMap.animateCamera(CameraUpdateFactory
					.zoomTo(MAP_MAX_ZOOM_LEVEL));
		}
		if (cameraPosition.zoom < MAP_MIN_ZOOM_LEVEL) {
			googleMap.animateCamera(CameraUpdateFactory
					.zoomTo(MAP_MIN_ZOOM_LEVEL));
		}
	}

	@Override
	public boolean onMarkerClick(Marker paramMarker) {
	
		Log.i("onMarkerClick", paramMarker.toString());
		return false;
	}

	@Override
	public boolean onMyLocationButtonClick() {
		
		Log.i("onMyLocationButtonClick", "clicked");
		showMyLocation();
		return false;
	}

	private void showMyLocation() {

		myLocationMarker.setTitle("我猜您的位置在这里");
		myLocationMarker.hideInfoWindow();
		myLocationMarker.showInfoWindow();
		googleMap.animateCamera(CameraUpdateFactory.newLatLng(myLocationMarker
				.getPosition()));
	}

	@Override
	public void onMapClick(LatLng paramLatLng) {
		
		Log.i("MapClicked", paramLatLng.toString());
	}
	
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tile_map_display, menu);
        return true;
    }
    
    public void onPause() {
    	System.exit(0);
    }

}
