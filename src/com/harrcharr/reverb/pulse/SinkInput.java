package com.harrcharr.reverb.pulse;

import android.util.Log;

public class SinkInput extends StreamNode {		
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
		return mName;
	}
	public Volume getVolume() {
		return mVolume;
	}
	public boolean isMuted() {
		return false;
	}
	
	public void setMute(boolean mute, SuccessCallback cb) {
		Log.d("Reverb", ""+(mPulse == null));
		mPulse.setSinkInputMute(mIndex, mute, cb);
	}
	
	public String toString() {
		return mName;
	}
	
	private final native void JNIPopulateStruct(long pSinkInputInfo);
}
