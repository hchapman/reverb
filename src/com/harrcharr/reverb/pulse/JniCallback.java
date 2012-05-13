package com.harrcharr.reverb.pulse;

public abstract class JniCallback implements Comparable<JniCallback> {
	private PulseContext mPulse;
	private long mGlobalRef;
	private boolean mReferenced;
	
	public void storeGlobal(PulseContext pulse, long ref) {
		mPulse = pulse;
		mGlobalRef = ref;
		mReferenced = true;
		
		mPulse.holdCallback(this);
	}
	
	public long getGlobal() {
		return mGlobalRef;
	}
	
	public void unstoreGlobal() {
		mPulse.unholdCallback(this);
		mReferenced = false;
	}
	
	public void freeGlobal() {
		if (mReferenced) {
			unstoreGlobal();
			JNIUtil.deleteGlobalRef(mGlobalRef);
		}
	}
	
	public int compareTo(JniCallback cb) {
		return (int)(getGlobal() - cb.getGlobal());
	}
}
