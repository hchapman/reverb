package com.harrcharr.reverb.pulse;

import java.util.HashMap;

public abstract class JNIObject {	
	private long mPointer;
	private static HashMap<Long, JNIObject> ptrTable = new HashMap<Long, JNIObject>();
	
	protected JNIObject(long ptr) {
		mPointer = ptr;
		addToTable();
	}
	protected void finalize() {
		purge();
	}
	
	/* 
	 * Delete object being pointed to, remove self from pointer table
	 */
	public synchronized void purge() {
		ptrTable.remove(new Long(mPointer));
	}
	
	public static JNIObject getByPointer(long ptr) {
		return getByPointer(new Long(ptr));
	}
	public static JNIObject getByPointer(Long ptr) {
		return ptrTable.get(ptr);
	}
	protected void addToTable() {
		ptrTable.put(new Long(mPointer), this);
	}
	public long getPointer() {
		return mPointer;
	}
}
