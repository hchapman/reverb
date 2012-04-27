package com.harrcharr.reverb.pulse;

public class SinkInputInfo extends JNIStruct {
	String sName;
	
	public SinkInputInfo(long ptr) {
		super(ptr);
	}
	
	protected void populate() {
		JNIPopulateStruct(getPointer());
	}
	public String getName() {
		return sName;
	}
	
	public String toString() {
		return sName;
	}
	
	private final native void JNIPopulateStruct(long pSinkInputInfo);
}
