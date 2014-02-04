package com.harrcharr.reverb;

import android.app.Application;

import com.harrcharr.reverb.pulseutil.HasPulseManager;
import com.harrcharr.reverb.pulseutil.PulseManager;

public class ReverbApplication extends Application
implements HasPulseManager {
	PulseManager mPulseManager;
	
	@Override
	public void onCreate() {
		mPulseManager = new PulseManager();
		mPulseManager.connect(ReverbSharedPreferences.getDefaultServer(this));
	}
	
    public PulseManager getPulseManager() {
    	return mPulseManager;
    }
}
