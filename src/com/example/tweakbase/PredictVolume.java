package com.example.tweakbase;

import android.app.Activity;
import android.content.Context;

public class PredictVolume extends Activity {
	
	DatabaseHandler db;
	
	public PredictVolume(Context con){
		db = new DatabaseHandler(con);
	}
	
	public void onPatternIdentified(){
		db.addRMProfile(new TBRingermodeProfiles(1.55,1.55,2,2,143,true));
	}
	
	

}
