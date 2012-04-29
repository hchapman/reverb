package com.harrcharr.reverb.pulse;

public class SinkInfo extends JNIStruct {
	String sName;
	String sDescription;
	
	public SinkInfo(long ptr) {
		super(ptr);
	}
	
	protected void populate() {
		JNIUpdateWithInfo(getPointer());
	}
	public String getDescription() {
		return sDescription;
	}
	
	public String toString() {
		return sName + "\n" + sDescription;
	}
	
	public native Volume getVolume();
	
	private final native void JNIUpdateWithInfo(long pSinkInfo);
}
