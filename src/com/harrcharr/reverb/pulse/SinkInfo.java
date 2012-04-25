package com.harrcharr.reverb.pulse;

public class SinkInfo extends JNIStruct {
	String sName;
	String sDescription;
	
	public SinkInfo(long ptr) {
		super(ptr);
	}
	
	protected void populate() {
		JNIPopulateStruct(getPointer());
	}
	
	public String toString() {
		return sName + "\n" + sDescription;
	}
	
	private final native void JNIPopulateStruct(long pSinkInfo);
}
