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
