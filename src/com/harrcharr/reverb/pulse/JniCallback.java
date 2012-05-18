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
		unstoreGlobal(true);
	}
	public void unstoreGlobal(boolean unhold) {
		if (unhold) {
			mPulse.unholdCallback(this);
		}
		mReferenced = false;
	}
	
	public void freeGlobal() {
		freeGlobal(true);
	}
	public void freeGlobal(boolean unhold) {
		if (mReferenced) {
			unstoreGlobal(unhold);
			JNIUtil.deleteGlobalRef(mGlobalRef);
		}
	}
	
	public int compareTo(JniCallback cb) {
		return (int)(getGlobal() - cb.getGlobal());
	}
}
