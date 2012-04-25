package com.harrcharr.reverb.pulse;

public class Context extends JNIObject {
	public Context(Mainloop m) {
		super(JNICreate(m.getPointer()));
	}
	
	public void connect(String servername) {
		JNIConnect(getPointer(), servername);
	}
	
	public static void contextStatusChanged(long pContext, int nStatus) {
		System.out.println("ho! Status of " + pContext + " is now "+ nStatus);
	}
	
	private static final native long JNICreate(long pMainloop);
	private static final native int JNIConnect(
			long pContext, String server);
}
