package com.example.tweakbase;

import android.app.Activity;
import android.os.Bundle;


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

	final String TAG = "SettingsActivity";

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
	}
}
