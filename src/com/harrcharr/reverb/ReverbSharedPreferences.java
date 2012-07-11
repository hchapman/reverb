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
}
