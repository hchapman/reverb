package com.harrcharr.reverb.pulse;

public class Context {
	long pContext;
	
	public Context(Mainloop m) {
		JNICreate(m.getPointer());
	}
	
	public void connect(String servername) {
		JNIConnect(servername);
	}
	
	public long getPointer() {
		return pContext;
	}
	
	public void callback() {
		System.out.println("ho!");
	}
	
	private final native void JNICreate(long ptr_mainloop);
	private final native int JNIConnect(String server);
}
