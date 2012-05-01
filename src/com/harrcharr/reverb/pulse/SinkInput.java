package com.harrcharr.reverb.pulse;

import android.util.Log;

public class SinkInput extends StreamNode {		
	// More optional parameters
	protected String mAppName;
	
	public SinkInput(PulseContext pulse, long ptr) {
		super(pulse, ptr);
	}
	
	public void update(long ptr) {
		JNIPopulateStruct(ptr);
	}
	
	public String getName() {
		return mName;
	}
	public String getDescriptiveName() {
		if (mAppName != null) 
			return mAppName + " - " + mName;
		return mName;
	}
	
	public String getAppName() {
		return mAppName;
	}

	public boolean isMuted() {
		return false;
	}
	
	public void setMute(boolean mute, SuccessCallback cb) {
		Log.d("Reverb", ""+(mPulse == null));
		mPulse.setSinkInputMute(mIndex, mute, cb);
	}
	public void setVolume(Volume volume, SuccessCallback cb) {
		Log.d("Reverb", ""+(mPulse == null));
		mPulse.setSinkInputVolume(mIndex, volume, cb);
	}
	
	public String toString() {
		return mName;
	}
	
	private final native void JNIPopulateStruct(long pSinkInputInfo);
}
