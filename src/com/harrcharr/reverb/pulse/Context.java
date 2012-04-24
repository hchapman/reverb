package com.harrcharr.reverb.pulse;

public class Context {
	long pContext;
	
	public Context(Mainloop m) {
		pContext = Context.JNICreate(m.getPointer());
	}
	
	public void connect(String servername) {
		Context.JNIConnect(pContext, servername);
	}
	
	public long getPointer() {
		return pContext;
	}
	
	private final static native long JNICreate(long ptr_mainloop);
	private final static native long JNIConnect(
			long ptr_context, String server);
}
