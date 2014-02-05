package com.example.tweakbase;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

public class AppTrackerReceiver extends BroadcastReceiver {
	private static final String TAG = "AppTrackerReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		ActivityManager am = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = context.getPackageManager();
		while(i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
			try {

				CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
				String appName = c.toString();
				if (!appName.equals("Google Play services") && !appName.equals("Settings") && !appName.equals("Google Search")) {

					Log.d(TAG, c.toString());
					break;
				}
			} catch(Exception e) {

			}
		}
	}

}
