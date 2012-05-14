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
	public static enum ContextState {
		UNCONNECTED,
		CONNECTING,
		AUTHORIZING,
		SETTING_NAME,
		READY,
		FAILED,
		TERMINATED;
		
		/*
		 *  Takes a value from C named PA_CONTEXT_[statename], 
		 *  returns Java equivalent. Take care to ensure consistency with
		 *  build version of libpulse headers.
		 */
		public static ContextState fromInt (int cValue) {
			switch(cValue) {
			case 0: return UNCONNECTED;
			case 1: return CONNECTING;
			case 2: return AUTHORIZING;
			case 3: return SETTING_NAME;
			case 4: return READY;
			case 5: return FAILED;
			case 6: return TERMINATED;
			}
			
			return null;
		}
	}
	
	protected Runnable cbStatusChanged;
	protected Mainloop mainloop;
	
	protected Runnable onConnecting;
	protected Runnable onAuthorizing;
	protected Runnable onSettingName;
	protected Runnable onReady;
	protected Runnable onFailure;
	protected Runnable onTerminate;
	
	protected TreeSet<JniCallback> mCallbacks;
	
	private boolean mSubscribed; 
	protected long mSubCbPtr;
	
	protected ByteBuffer mCbPtrs;
	
	protected ContextState mStatus;
	
	public PulseContext(Mainloop m) {
		super(JNICreate(m.getPointer()));
		mainloop = m;
		mStatus = ContextState.UNCONNECTED;
		mSubscribed = false;
		
		mCallbacks = new TreeSet<JniCallback>();
	}
	
	public long getMainloopPointer() {
		return mainloop.getPointer();
	}
	
	public final native void connect(String server)
		throws Exception;
	
	protected void subscribe() {
		if (mSubscribed)
			return;
		
		mSubCbPtr = JNISubscribe(getPointer(), mainloop.getPointer());
		mSubscribed = true;
	}
	private static final native long JNISubscribe(long pContext, long pMainloop);
	
	public void subscribeSinkInput(SubscriptionCallback cb) {
		subscribe();
		JNISubscribeSinkInput(getPointer(), mSubCbPtr, cb);
	}
	private static final native void JNISubscribeSinkInput(long pContext, long pCbs, SubscriptionCallback cb);
	
	
	public void setSinkInputVolume(int idx, Volume volume, SuccessCallback cb) {
		JNISetSinkInputVolumeByIndex(idx, volume.getVolumes(), cb);
	}
	
	public boolean isReady() {
		return mStatus == ContextState.READY;
	}
	
	public boolean isConnected() {
		return (getStatus() == 4);
	}
	
	protected void operationSuccess(int success) {

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
		setConnectionReadyCallback(null);
		subscribeSinkInput(null);
		
		// Free all possible remaining callbacks
		for (JniCallback callback : mCallbacks) {
			callback.freeGlobal();
		}
		
		disconnect();
	}
	
	protected final native void disconnect();
	
	public final native void setConnectionReadyCallback(NotifyCallback cb);
	
	public final native int getStatus();

	// Sink
	public final native void getSinkInfoByIndex(int idx, InfoCallback<SinkInfo> cb);
	public final native void setSinkMuteByIndex(int idx, boolean mute);
	
	// Sink Input
	public final native void getSinkInputInfo(int idx, InfoCallback<SinkInput> cb);
	public final native void getSinkInputInfoList(InfoCallback<SinkInput> cb);
	public final native void setSinkInputMute(int idx, boolean mute, SuccessCallback cb);
	private synchronized final native void JNISetSinkInputVolumeByIndex(int idx, int[] volumes, SuccessCallback cb);
	
	// Client
	public final native void getClientInfo(int idx, InfoCallback<ClientInfo> cb);
	public final native void getClientInfoList(InfoCallback<ClientInfo> cb);
}
