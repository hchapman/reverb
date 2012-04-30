package com.harrcharr.reverb.pulse;

public class PulseContext extends JNIObject {
	protected Runnable cbStatusChanged;
	protected Mainloop mainloop;
	
	protected long mSubCbPtr;
	
	protected int nStatus;
	
	public PulseContext(Mainloop m) {
		super(JNICreate(m.getPointer()));
		mainloop = m;
		nStatus = -1;
	}
	
	public void connect(String servername) {
		JNIConnect(getPointer(), servername);
		System.out.println("JNIConnect done.");
	}
	
	public void subscribe() {
		mSubCbPtr = JNISubscribe(getPointer(), mainloop.getPointer());
	}
	public void subscribeSinkInput(SubscriptionCallback cb) {
		JNISubscribeSinkInput(getPointer(), mSubCbPtr, cb);
	}
	
	// Sink Queries
	public void getSinkInfo(int idx, InfoCallback<SinkInfo> cb) {
		JNIGetSinkInfoByIndex(getPointer(), mainloop.getPointer(), idx, cb);		
	}
	// Sink Actions
	public void setSinkMute(int idx, boolean mute) {
		JNISetSinkMuteByIndex(getPointer(), mainloop.getPointer(), idx, mute);
	}
	
	// Sink Input Queries
	public void getSinkInputInfo(int idx, InfoCallback<SinkInput> cb) {
		JNIGetSinkInputInfo(getPointer(), mainloop.getPointer(), idx, cb);		
	}
	public void getSinkInputInfoList(InfoCallback<SinkInput> cb) {
		JNIGetSinkInputInfoList(getPointer(), mainloop.getPointer(), cb);		
	}
	// Sink Input Actions
	public void setSinkInputMute(int idx, boolean mute, SuccessCallback cb) {
		JNISetSinkInputMuteByIndex(getPointer(), mainloop.getPointer(), idx, mute);
	}
	
	// Client Queries
	public void getClientInfo(int idx, InfoCallback<ClientInfo> cb) {
		JNIGetClientInfo(getPointer(), mainloop.getPointer(), idx, cb);		
	}
	public void getClientInfoList(InfoCallback<ClientInfo> cb) {
		JNIGetClientInfoList(getPointer(), mainloop.getPointer(), cb);		
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
	
	protected void operationSuccess(int success) {

	}
	
	public static void statusChanged(long pContext, int status) {
		((PulseContext)JNIObject.getByPointer(pContext))
			.statusChanged(status);
	}
	
	private static final native long JNICreate(long pMainloop);
	private static final native int JNIConnect(long pContext, String server);
	
	private static final native long JNISubscribe(long pContext, long pMainloop);
	private static final native void JNISubscribeSinkInput(long pContext, long pCbs, SubscriptionCallback cb);
	
	// Sink
	private static final native void JNIGetSinkInfoByIndex(long pContext, long pMainloop, int idx, InfoCallback cb);
	private static final native void JNISetSinkMuteByIndex(long pContext, long pMainloop, int idx, boolean mute);
	
	// Sink Input
	private static final native void JNIGetSinkInputInfo(long pContext, long pMainloop, int idx, InfoCallback cb);
	private static final native void JNIGetSinkInputInfoList(long pContext, long pMainloop, InfoCallback cb);
	private static final native void JNISetSinkInputMuteByIndex(long pContext, long pMainloop, int idx, boolean mute);
	
	// Client
	private static final native void JNIGetClientInfo(long pContext, long pMainloop, int idx, InfoCallback cb);
	private static final native void JNIGetClientInfoList(long pContext, long pMainloop, InfoCallback cb);
}
