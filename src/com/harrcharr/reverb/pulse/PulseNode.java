package com.harrcharr.reverb.pulse;

import java.nio.ByteBuffer;

public abstract class PulseNode extends JNIStruct {
	public static String PROP_APPLICATION_NAME = "application.name";
	
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
