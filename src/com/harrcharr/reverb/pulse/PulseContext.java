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

import java.nio.ByteBuffer;
import java.util.TreeSet;

public class PulseContext extends JNIObject {
	protected Runnable cbStatusChanged;
	protected Mainloop mainloop;
	
	protected TreeSet<JniCallback> mCallbacks;
	
	protected long mSubCbPtr;
	
	protected ByteBuffer mCbPtrs;
	
	protected int nStatus;
	
	public PulseContext(Mainloop m) {
		super(JNICreate(m.getPointer()));
		mainloop = m;
		nStatus = -1;
		
		mCallbacks = new TreeSet<JniCallback>();
	}
	
	public final native void connect(String server)
		throws Exception;
	
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
		JNIGetSinkInputInfoList(mainloop.getPointer(), cb);		
	}
	// Sink Input Actions
	public void setSinkInputMute(int idx, boolean mute, SuccessCallback cb) {
		JNISetSinkInputMuteByIndex(getPointer(), mainloop.getPointer(), idx, mute);
	}
	public void setSinkInputVolume(int idx, Volume volume, SuccessCallback cb) {
		JNISetSinkInputVolumeByIndex(getPointer(), mainloop.getPointer(), idx, volume.getVolumes(), cb);
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
	
	public boolean isConnected() {
		return (getStatus() == 4);
	}
	
	protected void operationSuccess(int success) {

	}
	
	public static void statusChanged(long pContext, int status) {
		((PulseContext)JNIObject.getByPointer(pContext))
			.statusChanged(status);
	}
	
	public void holdCallback(JniCallback cb) {
		mCallbacks.add(cb);
	}
	public void unholdCallback(JniCallback cb) {
		mCallbacks.remove(cb);
	}
	
	private static final native long JNICreate(long pMainloop);
	
	/*
	 * Closes the Context, and frees all unneeded C objects.
	 */
	public void close() {
		setStateCallback(null);
		subscribeSinkInput(null);
		
		// Free all possible remaining callbacks
		for (JniCallback callback : mCallbacks) {
			callback.freeGlobal();
		}
		
		disconnect();
	}
	
	protected final native void disconnect();
	
	public final native void setStateCallback(NotifyCallback cb);
	
	public final native int getStatus();
	
	private static final native long JNISubscribe(long pContext, long pMainloop);
	private static final native void JNISubscribeSinkInput(long pContext, long pCbs, SubscriptionCallback cb);
	
	// Sink
	private static final native void JNIGetSinkInfoByIndex(long pContext, long pMainloop, int idx, InfoCallback cb);
	private static final native void JNISetSinkMuteByIndex(long pContext, long pMainloop, int idx, boolean mute);
	
	// Sink Input
	private static final native void JNIGetSinkInputInfo(long pContext, long pMainloop, int idx, InfoCallback cb);
	private final native void JNIGetSinkInputInfoList(long pMainloop, InfoCallback cb);
	private static final native void JNISetSinkInputMuteByIndex(long pContext, long pMainloop, int idx, boolean mute);
	private static synchronized final native void JNISetSinkInputVolumeByIndex(long pContext, long pMainloop, int idx, int[] volumes, SuccessCallback cb);
	
	// Client
	private static final native void JNIGetClientInfo(long pContext, long pMainloop, int idx, InfoCallback cb);
	private static final native void JNIGetClientInfoList(long pContext, long pMainloop, InfoCallback cb);
}
