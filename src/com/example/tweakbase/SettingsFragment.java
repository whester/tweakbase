package com.example.tweakbase;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.util.Log;

// TODO: Comment on this, allow for use when GPS is not on, save settings

/**
 * This class is the meat of SettingsActivity. It handles displaying TweakBase's preferences and 
 * dispatching the relevant actions based on user preference changes.
 * 
 * This class extends PreferenceFragment, an Android abstract class that makes displaying the typical
 * Android settings activities easy.
 * 
 * This class also implements the OnSharedPreferenceChangeListener interface. This interface requires
 * implementers to override the onSharedPreferenceChanged method, which is automatically called by the
 * Android OS whenever an element in this activity's SharedPreferences is changed. The elements that
 * will change correspond to the settings TweakBase's users will be able to change.
 */
public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	final String TAG = "SettingsFragment";
	static final String KEY_PREF_TRACK_LOCATION = "pref_trackLocation";
	boolean trackMyLocation;
	TBLocationListener locListener;
	LocationManager locManager;

	// The minimum time between updates in milliseconds
	private static final long TIME_BW_UPDATES = 1000 * 10; // 10 seconds

	/**
	 * Inherited from the PreferenceFragment class. Called by the Android OS when
	 * an activity is opened or a fragment is displayed.
	 * 
	 *  @param savedInstanceState	a Bundle (Android class that contains
	 *  saved file information) that the Android OS automatically passes
	 *  when this method is called.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		locListener = new TBLocationListener();

		// Load this activity's SharedPreferences and get the saved preferences
		SharedPreferences sharedPref = getPreferenceManager().getSharedPreferences();
		trackMyLocation = sharedPref.getBoolean(KEY_PREF_TRACK_LOCATION, true);

		if (trackMyLocation) {
			trackLocation();
		}    
	}

	/**
	 * Kicks off tracking location. Called when the user wants TweakBase to track his/her location.
	 * This method takes advantage of threads (if you don't know what threads are, ask Professor
	 * Badass). In Android, actions that take place in the background (like noting location every 
	 * 10 min.) are not permitted on the UI thread (the main thread an Activity runs on). 
	 * 
	 * So, we're creating our own thread that we can do whatever we want on. In this case, we're going 
	 * to give the thread a LocationManager and call it's requestLocaitonUpdates() method, which notifies
	 * a LocationListener on location updates.
	 * 
	 * LocationListener is an Android-defined interface with four methods. We have our own implementation,
	 * TBLocationListener. More info in that class.
	 */
	private void trackLocation() {
		Log.d(TAG, "Starting to track location");

		final Handler mHandler = new Handler();
		new Thread(new Runnable(){ public void run(){
			mHandler.post(new Runnable(){public void run(){
				locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
				// Requests location updates. Android OS knows to call upon locListener every TIME_BW_UPDATES ms
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_BW_UPDATES, 0, locListener);
			}});
		}}).start();
		Log.d(TAG, "Location tracking started");
	}

	 /** Overridden from PreferenceFragment. Automatically called by the Android OS. Necessary to notify
	 * changes to SharedPreferences to whoever needs it.
	 */
	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

	}

	 /** Overridden from PreferenceFragment. Automatically called by the Android OS. Necessary to notify
	 * changes to SharedPreferences to whoever needs it.
	 */
	@Override
	public void onPause() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	/**
	 * Implemented from OnSharedPreferenceChangeListener. Called when there is a change in this Activity's
	 * SharedPreferences.
	 * 
	 * @param sharedPreferences		the SharedPreferences object that was changed.
	 * @param key					the key within sharedPreferences that was changed.
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(KEY_PREF_TRACK_LOCATION)) {
			trackMyLocation = sharedPreferences.getBoolean(KEY_PREF_TRACK_LOCATION, true);

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
