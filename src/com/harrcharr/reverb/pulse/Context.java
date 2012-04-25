package com.harrcharr.reverb.pulse;

public class Context extends JNIObject {
	private Runnable cbStatusChanged;
	protected Mainloop mainloop;
	
	public Context(Mainloop m) {
		super(JNICreate(m.getPointer()));
		mainloop = m;
	}
	
	public void connect(String servername) {
		JNIConnect(getPointer(), servername);
		System.out.println("JNIConnect done.");
		JNIGetSinkInfoByIndex(getPointer(), mainloop.getPointer(), 0);		
	}
	
	public void statusChanged(int nStatus) {
		if (cbStatusChanged != null) {
			cbStatusChanged.run();
		}
		System.out.println("I'm pointing to "+getPointer());
		System.out.println("My status has changed to "+nStatus);
		
		if (nStatus >= 4) {

		}
	}
	
	public static void statusChanged(long pContext, int nStatus) {
		((Context)JNIObject.getByPointer(pContext))
			.statusChanged(nStatus);
	}
	
	public static void gotSinkInfo(long pContext, long pSinkInfo) {
		System.out.println(pSinkInfo);
		SinkInfo si = new SinkInfo(pSinkInfo);
		System.out.println(si);
	}
	
	private static final native long JNICreate(long pMainloop);
	private static final native int JNIConnect(
			long pContext, String server);
	private static final native void JNIGetSinkInfoByIndex(
			long pContext, long pMainloop, int idx);
}
