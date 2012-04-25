package com.harrcharr.reverb.pulse;

public class Context extends JNIObject {
	public Context(Mainloop m) {
		super(JNICreate(m.getPointer()));
	}
	
	public void connect(String servername) {
		JNIConnect(getPointer(), servername);
	}
	
	public void statusChanged(int nStatus) {
		System.out.println("I'm pointing to "+getPointer());
		System.out.println("My status has changed to "+nStatus);
	}
	
	public static void contextStatusChanged(long pContext, int nStatus) {
		((Context)JNIObject.getByPointer(pContext))
			.statusChanged(nStatus);
	}
	
	private static final native long JNICreate(long pMainloop);
	private static final native int JNIConnect(
			long pContext, String server);
}
