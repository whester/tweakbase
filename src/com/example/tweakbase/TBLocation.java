package com.example.tweakbase;

import java.util.Calendar;

public class TBLocation {
	private double latitude;
	private double longitude;
	private int dayOfWeek;
	private int intervalId;
	
	public TBLocation(double lat, double lon, int dow) {
		this.latitude = lat;
		this.longitude = lon;
		this.dayOfWeek = dow;
		this.intervalId = generateIntervalId();
	}
	
	public TBLocation() {
		
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double lat) {
		this.latitude = lat;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double lon) {
		this.longitude = lon;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public int getIntervalId() {
		return intervalId;
	}

	public void setIntervalId(int intervalId) {
		this.intervalId = intervalId;
	}
	
	public int generateIntervalId() {
		Calendar c = Calendar.getInstance();
		long now = c.getTimeInMillis();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long passed = now - c.getTimeInMillis();
		long secondsPassed = passed / 1000;
		long minutesPassed = secondsPassed / 60;
		return (int) minutesPassed / 5;
	}

}
