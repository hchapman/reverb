package com.harrcharr.reverb.pulse;

import java.util.HashMap;

public class JNIObject {	
	private long lPointer;
	private static HashMap<Long, JNIObject> ptrTable = new HashMap<Long, JNIObject>();
	
	protected JNIObject(long ptr) {
		lPointer = ptr;
	}
	
	public static JNIObject getByPointer(long ptr) {
		return getByPointer(new Long(ptr));
	}
	public static JNIObject getByPointer(Long ptr) {
		return ptrTable.get(ptr);
	}
	protected void addToTable() {
		ptrTable.put(new Long(lPointer), this);
	}
	public long getPointer() {
		return lPointer;
	}
}
