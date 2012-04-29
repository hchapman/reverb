package com.harrcharr.reverb.pulse;

public class SinkInput extends PulseNode {
	private int mIndex;
	private boolean mMuted;
	private Volume mVolume;
	private String mName;
	
	public SinkInput(PulseContext pulse, long ptr) {
		super(pulse, ptr);
	}
	
	protected void populate() {
		JNIPopulateStruct(getPointer());
	}
	
	public String getName() {
		return mName;
	}
	public Volume getVolume() {
		return mVolume;
	}
	public boolean isMuted() {
		return false;
	}
	
	public void setMute(boolean mute, SuccessCallback cb) {
		mPulse.setSinkInputMute(mIndex, mute, cb);
	}
	
	public String toString() {
		return mName;
	}
	
	private final native void JNIPopulateStruct(long pSinkInputInfo);
}
