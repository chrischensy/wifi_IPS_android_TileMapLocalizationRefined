package edu.xjtu.localization.displayv2.utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import edu.xjtu.localization.localization.utils.Point2D;

public class CoordinateTransformation {

	static double startx = 11.794336885213852, starty = -40.99265703415787;
	static double endx = 33.15680097788572, endy = -0.026869027;
	private static double lngScale = (endx - startx)/(19*0.8), latScale = (endy-starty)/(40*0.8);

	public static LatLng convert(Point2D p) {
		// TODO Auto-generated method stub
		double x = 0, y = 0;

		return new LatLng(x, y);
	}

	public static LatLng locationToLatLng(double x, double y) {
		// TODO Auto-generated method stub

		Log.i("location","xy:"+x+" "+y);
		
		double rx = startx + x * lngScale;
		double ry = starty + y * latScale;
		
		Log.i("location","latlng:"+rx+" "+ry);
		return new LatLng(ry, rx);
	}
	
	public static LatLng getMapCenter() {
		return new LatLng((starty+endy)/2, (startx +endx)/2);
	}

}
