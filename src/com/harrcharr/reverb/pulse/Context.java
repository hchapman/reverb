package com.harrcharr.reverb.pulse;

public class Context extends JNIObject {
	protected Runnable cbStatusChanged;
	protected SinkInfoCallback cbGotSinkInfo;
	protected SuccessCallback cbSuccess;
	protected Mainloop mainloop;
	
	protected int nStatus;
	
	public Context(Mainloop m) {
		super(JNICreate(m.getPointer()));
		mainloop = m;
		nStatus = -1;
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
	public void setSinkMute(int idx, boolean mute) {
		JNISetSinkMuteByIndex(getPointer(), mainloop.getPointer(), idx, mute);
	}
	
	public boolean isReady() {
		return nStatus == 4;
	}
	
	protected void statusChanged(int status) {
		nStatus = status;
		if (cbStatusChanged != null) {
			cbStatusChanged.run();
		}
		
	}
	
	protected void gotSinkInfo(SinkInfo si) {
		if (cbGotSinkInfo != null) {
			cbGotSinkInfo.run(si);
		}
	}
	
	protected void operationSuccess(int success) {
		if (cbSuccess != null) {
			cbSuccess.run(success);
		}
	}
	
	public static void statusChanged(long pContext, int status) {
		((Context)JNIObject.getByPointer(pContext))
			.statusChanged(status);
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
	private static final native void JNISetSinkMuteByIndex(
			long pContext, long pMainloop, int idx, boolean mute);
	
	public static interface SinkInfoCallback {
		void run(SinkInfo info);
	}
	public static interface SuccessCallback {
		void run(int success);
	}
}
