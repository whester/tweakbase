package com.example.tweakbase;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * This class implements the LocationListener interface. This interface requires implementers to 
 * override four methods that are automatically called by Android OS's LocationManager class if 
 * requested. The only method we'll use is onLocationChanged, which is called by the
 * LocationManager we created in SettingsFragment every time interval we specified.
 */
public class TBLocationListener implements LocationListener {
	
	private String TAG = "TBLocationListener";
	
	/**
	 * Overridden from LocationListener. In our case, called by LocationManager at the interval
	 * specified in SettingsFragment.
	 */
	@Override
	public void onLocationChanged(Location location) {	
		Log.d(TAG, "Latitude: "+ location.getLatitude()+" Longitude: "+ location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// Not necessary to do anything
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// Not necessary to do anything
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// Not necessary to do anything
	}

}
