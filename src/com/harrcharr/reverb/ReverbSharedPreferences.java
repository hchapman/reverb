package com.harrcharr.reverb;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ReverbSharedPreferences {
	public final static String PREFS_NAME = "reverb_prefs";

	public static String getDefaultServer(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return prefs.getString(
				context.getString(R.string.prefs_key_default_server),
				"192.168.0.5");
	}

	public static void setDefaultServer(Context context, String newValue) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		Editor prefsEditor = prefs.edit();
		prefsEditor.putString(
				context.getString(R.string.prefs_key_default_server),
				newValue);
		prefsEditor.commit();
	}
	
	public static boolean displayPeaks(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return prefs.getBoolean(
				context.getString(R.string.prefs_key_display_vol_peaks), 
				false);
	}
	
	public static void setDisplayPeaks(Context context, boolean newValue) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		Editor prefsEditor = prefs.edit();
		prefsEditor.putBoolean(
				context.getString(R.string.prefs_key_display_vol_peaks),
				newValue);
		prefsEditor.commit();
	}
	
	public static void registerOnSharedPreferenceChangeListener(Context context,
			SharedPreferences.OnSharedPreferenceChangeListener listener) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		prefs.registerOnSharedPreferenceChangeListener(listener);
	}
	
	public static void unregisterOnSharedPreferenceChangeListener(Context context,
			SharedPreferences.OnSharedPreferenceChangeListener listener) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		prefs.unregisterOnSharedPreferenceChangeListener(listener);
	}
}
