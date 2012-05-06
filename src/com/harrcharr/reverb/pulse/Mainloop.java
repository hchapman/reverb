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
