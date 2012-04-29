package com.harrcharr.reverb.pulse;

public class Volume extends JNIObject {
	private long mPointer;
	
	public Volume(long ptr) {
		super(ptr);
	}
	public synchronized void purge() {
		super.purge();
		free();
	}
//	public Volume(char channels, int[] values) {
//		// init it somehow maybe
//	}
	
	public final native int getMax(); 
	
	private synchronized final native void free();
}
