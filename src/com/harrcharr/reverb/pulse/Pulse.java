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

public class Pulse {
	long ptr_mainloop;
	long ptr_context;
	
	public Pulse() {

	}
	
	
	
//	public void alloc() {
//		ptrRTMP = Pulse.JNIAlloc();
//	}
//	
//	public void init() {
//		Pulse.JNIInit(ptrRTMP);
//	}
//	
//	public void setupUrl(String url) throws Exception {
//		if (JNISetupURL(ptrRTMP, url) != 1) {
//			throw new Exception("Error parsing RTMP URL");
//		}
//	}
//	
//	public void connect() throws Exception{
//		if (JNIConnect(ptrRTMP, 0) == 0) {
//			throw new Exception("Error connecting");
//		}
//		if (JNIConnectStream(ptrRTMP, 0) == 0) {
//			throw new Exception("Error connecting to stream");
//		}
//	}
//	
//	public int read(byte[] buf) {
//		return JNIRead(ptrRTMP, buf, 128);
//	}
//	
//	// JNI methods to librtmp
//
//
//	private final static native int JNIDoStuff();
//	private final static native int JNIFreeLoop();
//	private final static native void JNIInit(long ptrRTMP);
//	private final static native int JNISetupURL(long ptrRTMP, String url);
//	private final static native int JNIConnect(long ptrRTMP, long ptrPacket);
//	private final static native int JNIConnectStream(long ptrRTMP, int seek);
//	private final static native int JNIRead(long ptrRTMP, byte[] buf, int len);
}
