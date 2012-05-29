package com.harrcharr.reverb;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ReverbPreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(
                ReverbSharedPreferences.PREFS_NAME);
        addPreferencesFromResource(R.xml.prefs);
    }
}
