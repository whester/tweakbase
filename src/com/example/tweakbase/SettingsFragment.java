package com.example.tweakbase;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.util.Log;

// TODO: Save stuff to DB, upload data, get location on silence, use Places API

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
 * 
 * @author Will Hester
 */
public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	final String TAG = "SettingsFragment";
	static final String KEY_PREF_TRACK_LOCATION = "pref_trackLocation";
	static final String KEY_TRACKING = "trackingLocation";
	boolean trackMyLocation;
	boolean currentlyTracking;
	LocationManager locManager;
	Activity settingsActivity;
	TBLocationListener locListener;
	BroadcastReceiver volumeReceiver;

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

		settingsActivity = getActivity();

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		locListener = new TBLocationListener();

		volumeReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				AudioManager am = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
				switch (am.getRingerMode()) {
				case AudioManager.RINGER_MODE_SILENT:
					Log.i("TweakBase","Silent mode");
					break;
				case AudioManager.RINGER_MODE_VIBRATE:
					Log.i("TweakBase","Vibrate mode");
					break;
				case AudioManager.RINGER_MODE_NORMAL:
					Log.i("TweakBase","Normal mode");
					break;
				}
			}
		};
		IntentFilter filter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
		getActivity().registerReceiver(volumeReceiver, filter);

		// Load this activity's SharedPreferences and get the saved preferences
		SharedPreferences sharedPref = getPreferenceManager().getSharedPreferences();
		trackMyLocation = sharedPref.getBoolean(KEY_PREF_TRACK_LOCATION, true);
		currentlyTracking = sharedPref.getBoolean(KEY_TRACKING, false);

		if (trackMyLocation) {
			trackLocation();
		}
	}

	/**
	 * Kicks off tracking location. Called when the user wants TweakBase to track his/her location.
	 * This method takes advantage of threads (if you don't know what threads are, ask Professor
	 * Badass). In Android, actions that take place in the background (like recording location every 
	 * 10 min.) are not permitted on the UI thread (the main thread an Activity runs on). 
	 * 
	 * So, we're creating our own thread that we can do whatever we want on. In this case, we're going 
	 * to give the thread a LocationManager and call its requestLocaitonUpdates() method, which notifies
	 * a LocationListener on location updates.
	 * 
	 * LocationListener is an Android-defined interface with four methods. We have our own implementation,
	 * TBLocationListener. More info in that class.
	 */
	private void trackLocation() {
		if (!currentlyTracking) {
			Log.d(TAG, "Starting to track location");
			// We want to get updates every time the clock ends in a 0 or a 5 (every five minutes).
			// This sleeps the thread until the next time that happens
			Calendar c = Calendar.getInstance();
			Date now = new Date();
			c.setTime(now);
			int unroundedMinutes = c.get(Calendar.MINUTE);
			int mod = unroundedMinutes % 5;
			c.add(Calendar.MINUTE, mod == 0 ? 5 : 5 - mod);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			final long timeToWait = (c.getTimeInMillis()-now.getTime());
			final Handler mHandler = new Handler();
			Thread locationThread = new Thread(new Runnable(){ public void run(){
				mHandler.postDelayed(new Runnable(){ public void run() {
					locManager = (LocationManager) settingsActivity.getSystemService(Context.LOCATION_SERVICE);
					boolean isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);				
					if (isGPSEnabled) {
						// Requests location updates from GPS. Android OS knows to call upon locListener every TIME_BW_UPDATES ms
						locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_BW_UPDATES, 0, locListener);
					} else {
						// Requests location updates from network.
						locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_BW_UPDATES, 0, locListener);
					}
					Log.d(TAG, "Location tracking started");
				}}, timeToWait);
			}});
			locationThread.start();
			Log.d(TAG, "Sleeping until..." + c.getTime());
		}
	}

	/** 
	 * Overridden from PreferenceFragment. Automatically called by the Android OS. Necessary to notify
	 * changes to SharedPreferences to whoever needs it.
	 */
	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

	}

	/** 
	 * Overridden from PreferenceFragment. Automatically called by the Android OS. Necessary to notify
	 * changes to SharedPreferences to whoever needs it.
	 */
	@Override
	public void onPause() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	/**
	 * Overridden from PreferenceFragment. Automatically called by the Android OS. Also called when screen
	 * orientation changes. This saves rather or not the app is currently tracking location.
	 */
	@Override
	public void onDestroy() {
		SharedPreferences sharedPref = getPreferenceManager().getSharedPreferences();
		sharedPref.edit().putBoolean(KEY_TRACKING, trackMyLocation).commit();
		getActivity().unregisterReceiver(volumeReceiver);
		super.onDestroy();
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
