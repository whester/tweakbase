package com.example.tweakbase;

import java.util.Calendar;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

@Deprecated
public class TBLocationListener implements LocationListener {
	
	private String TAG = "TBLocationListener";
	DatabaseHandler db;
	
	public TBLocationListener(Context con) {
		db = new DatabaseHandler(con);
	}
	
	@Override
	public void onLocationChanged(Location location) {	
		Calendar c = Calendar.getInstance();
		db.addLocation(new TBLocation(location.getLatitude(), location.getLongitude(), c.get(Calendar.DAY_OF_WEEK)));
		Log.d(TAG, "SQLite num. entries: " + db.getAllLocations().size());
		Log.d(TAG, "DOW: " + db.getAllLocations().get(0).getDayOfWeek());
		Log.d(TAG, "Latitude: "+ location.getLatitude()+" Longitude: "+ location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// Required to override, but do nothing
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// Required to override, but do nothing
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// Required to override, but do nothing
	}
}
