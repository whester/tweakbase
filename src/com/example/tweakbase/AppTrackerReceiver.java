package com.example.tweakbase;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

public class AppTrackerReceiver extends BroadcastReceiver {
	private static final String TAG = "AppTrackerReceiver";
	static boolean foundHome = false;
	static String homeApp;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!foundHome) {
			Intent i = new Intent(); 
	        i.setAction(Intent.ACTION_MAIN); 
	        i.addCategory(Intent.CATEGORY_HOME); 
	        PackageManager pm = context.getPackageManager(); 
	        ResolveInfo ri = pm.resolveActivity(i, 0); 
	        ActivityInfo ai = ri.activityInfo; 
	        homeApp = ai.packageName;
	        Log.d(TAG, "New homeApp decided on: " + homeApp);
	        foundHome = true;
		}
		
		ActivityManager am = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> l = am.getRunningAppProcesses();
		Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
		PackageManager pm = context.getPackageManager();
		while(i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
			try {
				if (! (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)) {
					continue;
				}
				CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
				if (pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA).packageName.equals(homeApp)) {
					Log.d(TAG, "At home screen, quitting.");
					break;
				}
				Log.d(TAG, c.toString());
				break;
			} catch(Exception e) {

			}
		}
	}

}
