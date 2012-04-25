package com.harrcharr.reverb.pulse;

public abstract class JNIStruct extends JNIObject {
	protected JNIStruct(long ptr) {
		super(ptr);
		if (ptr != 0) {
			populate();
		}
	}
	protected abstract void populate();
}
