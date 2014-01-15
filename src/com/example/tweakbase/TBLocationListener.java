package com.example.tweakbase;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class TBLocationListener implements LocationListener {
	
	private String TAG = "TBLocationListener";
	
	@Override
	public void onLocationChanged(Location location) {	
		Log.d(TAG, "Latitude: "+ location.getLatitude()+" Longitude: "+ location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String arg0) {
		Log.d(TAG, "Disabled...");

	}

	@Override
	public void onProviderEnabled(String arg0) {
		Log.d(TAG, "Enabled...");

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		Log.d(TAG, "Status changed...");

	}

}
