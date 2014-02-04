package com.harrcharr.reverb;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class ReverbPreferencesActivity extends SherlockPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(
                ReverbSharedPreferences.PREFS_NAME);
        addPreferencesFromResource(R.xml.prefs);
    }
}
