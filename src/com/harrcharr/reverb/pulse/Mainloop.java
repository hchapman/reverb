package com.harrcharr.reverb.pulse;

public class Mainloop {
	long pMainloop;
	
	public Mainloop() {
		pMainloop = Mainloop.JNINew();
		Mainloop.JNIStart(pMainloop);
	}
	
	public long getPointer() {
		return pMainloop;
	}
	
	private final static native long JNINew();	
	private final static native long JNIStart(long pMainloop);
}
