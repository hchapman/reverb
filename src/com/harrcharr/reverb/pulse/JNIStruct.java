package com.harrcharr.reverb.pulse;

public abstract class JNIStruct {
	protected JNIStruct(long ptr) {
		if (ptr != 0) {
			update(ptr);
		}
	}
	public abstract void update(long ptr);
}
