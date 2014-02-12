package com.example.tweakbase;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.util.Log;

public class VolumeReceiver extends BroadcastReceiver {
	private static final String TAG = "VolumeReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
			DatabaseHandler db = new DatabaseHandler(context);
			Calendar cal = Calendar.getInstance();

			LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE); 
			// TODO: Implement a GPS check when it is turned on
			Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			double longitude = location.getLongitude();
			double latitude = location.getLatitude();

			AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

			// storing everything in the database
			db.addRingermode(new TBRingermode(latitude, longitude, cal.get(Calendar.DAY_OF_WEEK), am.getRingerMode()));
			//	addPattern.onPatternIdentified();
			switch (am.getRingerMode()) {
			case AudioManager.RINGER_MODE_SILENT:
				Log.i(TAG, "Phone is in Silent mode");
				break;
			case AudioManager.RINGER_MODE_VIBRATE:
				Log.i(TAG, "Phone is in Vibrate mode");
				break;
			case AudioManager.RINGER_MODE_NORMAL:
				Log.i(TAG, "Phone is in Normal mode");
				break;
			}

			Log.d(TAG, "Latitude: "+ location.getLatitude()+" Longitude: "+ location.getLongitude());
	}

}
