package com.example.tweakbase;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.tweakbase.LocationService.LocalBinder;


/**
 * SettingsActivity is meant to deal with displaying TweakBase's setting 
 * preferences. Currently, the only setting is allowing the application
 * to track the user's location or not with a checkbox. This class extends
 * Activity, an Android class.
 * 
 * @author Will Hester
 *
 */
public class SettingsActivity extends Activity {
	LocationService locationService;
	boolean mBound = false;
	
	final String TAG = "SettingsActivity";
	public static final String KEY_PREF_TRACK_LOCATION = "pref_trackLocation";
	
	// Listener for if a preference is changed
	OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
  	  public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
  	    if (key.equals(KEY_PREF_TRACK_LOCATION)) {
  	    	Log.d(TAG, "Track location preference changed");
  	    }
  	  }
  	};
	
  	/**
  	 * Inherited from the Activity class. Called by the Android OS when
  	 * the activity is opened.
  	 * 
  	 *  @param savedInstanceState	a Bundle (Android class that contains
  	 *  saved file information) that the Android OS automatically passes
  	 *  when this method is called.
  	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        
        // Load this activity's SharedPreferences and get the saved preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean trackLocation = sharedPref.getBoolean(KEY_PREF_TRACK_LOCATION, true);
        
        Log.d(TAG, "trackLocation is " + trackLocation);
        if (trackLocation) {
        	Log.d(TAG, "Trying to bind to LocationService...");
        	bindToLocationService();
        } else {
        	// Unbind from the service
            if (mBound) {
                unbindService(mConnection);
                mBound = false;
                Log.d(TAG, "SettingsActivity unbound from LocationService");
            }
        }

        // Tells sharedPref to respond to our listener
        sharedPref.registerOnSharedPreferenceChangeListener(listener);
    }
    
    /**
     * Helper method that binds this activity to the LocationService
     * activity. This method is called in onCreate.
     */
    private void bindToLocationService() {
    	// Bind to LocationService
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocationService instance
            LocalBinder binder = (LocalBinder) service;
            locationService = binder.getService();
            mBound = true;
            Log.d(TAG, "SettingsActivity bound to LocationService");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
  
}
