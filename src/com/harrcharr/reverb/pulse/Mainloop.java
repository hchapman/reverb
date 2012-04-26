package com.harrcharr.reverb.pulse;

public class Mainloop extends JNIObject {
	public Mainloop() {
		super(Mainloop.JNINew());
		Mainloop.JNIStart(getPointer());
		
	}
	private final static native long JNINew();	
	private final static native long JNIStart(long pMainloop);
}
