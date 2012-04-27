package com.harrcharr.reverb.pulse;

public class ClientInfo extends JNIStruct {
	String sName;
	
	public ClientInfo(long ptr) {
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
	
	private final native void JNIPopulateStruct(long pClientInfo);
}
