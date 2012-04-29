package com.harrcharr.reverb.pulse;

public class Mainloop extends JNIObject {
	static {
		System.loadLibrary("json");
		System.loadLibrary("sndfile");
		System.loadLibrary("pulsecommon-UNKNOWN.UNKNOWN");
		System.loadLibrary("pulse");
		System.loadLibrary("pulse_interface");
	}
	
	public Mainloop() {
		super(Mainloop.JNINew());
		Mainloop.JNIStart(getPointer());
		
	}
	private final static native long JNINew();	
	private final static native long JNIStart(long pMainloop);
}
