/*******************************************************************************
 * Copyright (c) 2012 Harrison Chapman.
 * 
 * This file is part of Reverb.
 * 
 *     Reverb is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 2 of the License, or
 *     (at your option) any later version.
 * 
 *     Reverb is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Reverb.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Harrison Chapman - initial API and implementation
 ******************************************************************************/
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
