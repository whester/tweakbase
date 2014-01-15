package com.example.tweakbase;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.util.Log;

// TODO: Comment on this, allow for use when GPS is not on

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	
	final String TAG = "SettingsFragment";
	static final String KEY_PREF_TRACK_LOCATION = "pref_trackLocation";
	boolean trackMyLocation;
	LocationManager locManager;

	// The minimum time between updates in milliseconds
	private static final long TIME_BW_UPDATES = 1000 * 10; // 10 seconds

	TBLocationListener locListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        locListener = new TBLocationListener();
        
        // Load this activity's SharedPreferences and get the saved preferences
        SharedPreferences sharedPref = getPreferenceManager().getSharedPreferences();
		trackMyLocation = sharedPref.getBoolean(KEY_PREF_TRACK_LOCATION, true);
		
		Log.d(TAG, "In onCreate, preference read as: " + trackMyLocation);
		
		if (trackMyLocation) {
			trackLocation();
		}
        
    }
    
    private void trackLocation() {
		Log.d(TAG, "Starting to track location");
		final Handler mHandler = new Handler();
		Thread locationThread = new Thread(new Runnable(){ public void run(){
			mHandler.post(new Runnable(){public void run(){
				locManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_BW_UPDATES, 0, locListener);
			}});
		}});
		locationThread.start();
		Log.d(TAG, "Location tracking started");
	}
    
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(KEY_PREF_TRACK_LOCATION)) {
			Log.d(TAG, "Location tracking preference changed");
			trackMyLocation = sharedPreferences.getBoolean(KEY_PREF_TRACK_LOCATION, true);
			Log.d(TAG, "In onCreate, preference read as: " + trackMyLocation);
			
			if (!trackMyLocation) {
				if (locManager != null) {
					locManager.removeUpdates(locListener);
				}
				Log.d(TAG, "Locaiton tracking stopped");
			} else {
				trackLocation();
			}
		}
	}
    
}
