package com.example.tweakbase;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class TBLocationChangedReceiver extends BroadcastReceiver {

	protected static String TAG = "TBLocationChangedReceiver";

	/**
	 * When a new location is received, extract it from the Intent and use
	 * it to start the Service used to update the list of nearby places.
	 * 
	 * This is the Passive receiver, used to receive Location updates from 
	 * third party apps when the Activity is not visible. 
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String key = LocationManager.KEY_LOCATION_CHANGED;
		Location location = null;

		if (intent.hasExtra(key)) {
			location = (Location)intent.getExtras().get(key);    
			DatabaseHandler db = new DatabaseHandler(context);
			Calendar c = Calendar.getInstance();
			db.addLocation(new TBLocation(location.getLatitude(), location.getLongitude(), c.get(Calendar.DAY_OF_WEEK)));
			Log.d(TAG, "SQLite num. entries: " + db.getAllLocations().size());
			Log.d(TAG, "DOW: " + db.getAllLocations().get(0).getDayOfWeek());
			Log.d(TAG, "Latitude: "+ location.getLatitude()+" Longitude: "+ location.getLongitude());
		}
	}
}
