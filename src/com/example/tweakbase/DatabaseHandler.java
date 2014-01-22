package com.example.tweakbase;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "tweakbaseDatabase";

	// Table name
	private static final String TABLE_LOCATION = "location";

	// Table Columns names
	private static final String KEY_LOC_ID = "location_id";
	private static final String KEY_LOC_LAT = "location_lat";
	private static final String KEY_LOC_LON = "location_lon";
	
	private static final String KEY_LOC_INTERVAL_ID = "location_interval_id";
	private static final String KEY_LOC_DAY_OF_WEEK = "location_day_of_week";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_LOCATION + "("
				+ KEY_LOC_ID + " INTEGER PRIMARY KEY," + KEY_LOC_LAT + " DOUBLE,"
				+ KEY_LOC_LON + " DOUBLE" + "," + KEY_LOC_INTERVAL_ID + " INTEGER,"
				+ KEY_LOC_DAY_OF_WEEK + " INTEGER" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);

		// Create tables again
		onCreate(db);
	}

	public void addLocation(TBLocation location) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_LOC_LAT, location.getLatitude());
		values.put(KEY_LOC_LON, location.getLongitude());
		values.put(KEY_LOC_INTERVAL_ID, location.getIntervalId());
		values.put(KEY_LOC_DAY_OF_WEEK, location.getDayOfWeek());

		// Inserting Row
		db.insert(TABLE_LOCATION, null, values);
		db.close(); // Closing database connection
	}
	
	public List<TBLocation> getAllLocations() {
	    List<TBLocation> locList = new ArrayList<TBLocation>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_LOCATION;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	TBLocation location = new TBLocation();
	        	location.setLatitude(cursor.getDouble(1));
	        	location.setLongitude(cursor.getDouble(2));
	        	location.setIntervalId(cursor.getInt(3));
	        	location.setDayOfWeek(cursor.getInt(4));
	            // Adding location to list
	            locList.add(location);
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    return locList;
	}
}
