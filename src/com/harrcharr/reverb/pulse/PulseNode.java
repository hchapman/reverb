package com.harrcharr.reverb.pulse;

public abstract class PulseNode extends JNIStruct {
	protected PulseContext mPulse;

	protected int mIndex;
	protected int mOwnerModule;
	
	protected String mName;
	protected String mDriver;
	
	public PulseNode(PulseContext pulse, long iPtr) {
		super(iPtr);
		mPulse = pulse;
	}
	
	public int getIndex() {
		return mIndex;
	}
	
	/*
	 * Returns a human-readable name for this PulseNode.
	 */
	public abstract String getDescriptiveName();

}
