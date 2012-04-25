package com.harrcharr.reverb.pulse;

public class Context extends JNIObject {
	protected Runnable cbStatusChanged;
	protected SinkInfoCallback cbGotSinkInfo;
	protected Mainloop mainloop;
	
	public Context(Mainloop m) {
		super(JNICreate(m.getPointer()));
		mainloop = m;
	}
	
	public void connect(String servername) {
		JNIConnect(getPointer(), servername);
		System.out.println("JNIConnect done.");
	}
	
	public void setGotSinkInfoCb(SinkInfoCallback cb) {
		cbGotSinkInfo = cb;
	}
	public void getSinkInfo(int idx, SinkInfoCallback cb) {
		setGotSinkInfoCb(cb);
		getSinkInfo(idx);
	}
	public void getSinkInfo(int idx) {
		JNIGetSinkInfoByIndex(getPointer(), mainloop.getPointer(), idx);		
	}
	
	protected void statusChanged(int nStatus) {
		if (cbStatusChanged != null) {
			cbStatusChanged.run();
		}
		System.out.println("I'm pointing to "+getPointer());
		System.out.println("My status has changed to "+nStatus);
		
		if (nStatus >= 4) {

		}
	}
	
	protected void gotSinkInfo(SinkInfo si) {
		if (cbGotSinkInfo != null) {
			cbGotSinkInfo.run(si);
		}
	}
	
	public static void statusChanged(long pContext, int nStatus) {
		((Context)JNIObject.getByPointer(pContext))
			.statusChanged(nStatus);
	}
	
	public static void gotSinkInfo(long pContext, long pSinkInfo) {
		((Context)JNIObject.getByPointer(pContext))
			.gotSinkInfo(new SinkInfo(pSinkInfo));
	}
	
	private static final native long JNICreate(long pMainloop);
	private static final native int JNIConnect(
			long pContext, String server);
	private static final native void JNIGetSinkInfoByIndex(
			long pContext, long pMainloop, int idx);
	
	public static interface SinkInfoCallback {
		void run(SinkInfo info);
	}
}
