package com.harrcharr.reverb.pulseutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.harrcharr.pulse.Mainloop;
import com.harrcharr.pulse.NotifyCallback;
import com.harrcharr.pulse.PulseContext;
import com.harrcharr.pulse.SinkInfo;
import com.harrcharr.pulse.SinkInfoCallback;
import com.harrcharr.pulse.SinkInput;
import com.harrcharr.pulse.SinkInputInfoCallback;
import com.harrcharr.pulse.SourceOutput;
import com.harrcharr.pulse.SourceOutputInfoCallback;
import com.harrcharr.pulse.SubscriptionCallback;

/**
 * @author harrcharr
 * A PulseManager has a connection to a PulseAudio context. It manages
 * lists of pulse info nodes, and should inform its listeners when
 * either pulse connection status changes, or the lists of nodes change.
 */
public class PulseManager {
	private Mainloop mPulseMainloop;
	private PulseContext mPulse;
	private List<PulseConnectionListener> mPulseConnectionListeners =
			new ArrayList<PulseConnectionListener>();	
	
	private List<SinkEventListener> mSinkEventListeners =
			new ArrayList<SinkEventListener>();
	private List<SinkInputEventListener> mSinkInputEventListeners =
			new ArrayList<SinkInputEventListener>();
	private List<SourceOutputEventListener> mSourceOutputEventListeners =
			new ArrayList<SourceOutputEventListener>();

	
	private String mServer;
	
	protected Map<Integer, SinkInfo> mSinks;
	//protected HashMap<Integer, SourceInfo> mSources;
	protected Map<Integer, SinkInput> mSinkInputs;
	protected Map<Integer, SourceOutput> mSourceOutputs;
	
	public PulseManager() {
		mPulseMainloop = new Mainloop();
		
		mSinks = Collections.synchronizedMap(new HashMap<Integer, SinkInfo>());
		mSinkInputs = Collections.synchronizedMap(new HashMap<Integer, SinkInput>());
		mSourceOutputs = Collections.synchronizedMap(new HashMap<Integer, SourceOutput>());
	}
	
	public PulseManager(String server) {
		this();
		connect(server);
	}
	
    public synchronized void connect(final String server) {
    	mServer = server;
    	
    	if(mPulse != null && mPulse.isConnected()) {
    		mPulse.close();
    	}
    	
    	mPulse = new PulseContext(mPulseMainloop);

    	mPulse.setConnectionReadyCallback(new NotifyCallback() {
    		@Override
    		public void run() {
    			subscribeToPulse();
				onPulseConnectionReady();
    		}
    	});
    	
    	mPulse.setConnectionFailedCallback(new NotifyCallback() {
    		@Override
    		public void run() {	
    			onPulseConnectionFailed();
    		}
    	});
    	
    	try {
			mPulse.connect(server);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private synchronized void subscribeToPulse() {
    	// Subscribe to sink updates
    	getPulseContext().subscribeSink(new SubscriptionCallback() {
    		public void run(int type, int index) {
    			Log.d("SinkSubscriptionCallback", type + ", index: " + index);
    			if (type == EVENT_REMOVE) {
    				onSinkRemoved(index);
    			} else {
    				getPulseContext().getSinkInfo(index, new SinkInfoCallback() {
    					public void run(SinkInfo i) {
    						onSinkUpdated(i);
    					}
    				});
    			}
    		}
    	});
    	
    	// Subscribe to sink input updates
    	getPulseContext().subscribeSinkInput(new SubscriptionCallback() {
    		public void run(int type, int index) {
    			Log.d("SinkInputSubscriptionCallback", type + ", index: " + index);
    			if (type == EVENT_REMOVE) {
    				onSinkInputRemoved(index);
    			} else {
    				getPulseContext().getSinkInputInfo(index, new SinkInputInfoCallback() {
    					public void run(SinkInput i) {
    						onSinkInputUpdated(i);
    					}
    				});
    			}
    		}
    	});
    	
    	// Subscribe to source output updates
    	getPulseContext().subscribeSourceOutput(new SubscriptionCallback() {
    		public void run(int type, int index) {
    			Log.d("SourceOutputSubscriptionCallback", type + ", index: " + index);
    			if (type == EVENT_REMOVE) {
    				onSourceOutputRemoved(index);
    			} else {
    				getPulseContext().getSourceOutputInfo(index, new SourceOutputInfoCallback() {
    					public void run(SourceOutput i) {
    						onSourceOutputUpdated(i);
    					}
    				});
    			}
    		}
    	});
    }
    
    public String getServerName() {
    	return mServer;
    }
	
    public PulseContext getPulseContext() {
    	return mPulse;
    }

    public void addOnSinkEventListener(SinkEventListener l) {
    	synchronized(mSinkEventListeners) {
    		mSinkEventListeners.add(l);
    	}
    }
    public void removeOnSinkEventListener(SinkEventListener l) {
    	synchronized(mSinkEventListeners) {
    		mSinkEventListeners.remove(l);
    	}
    }

    private void onSinkUpdated(final SinkInfo node) {
		final Integer key = new Integer(node.getIndex());
		final boolean isNew = !mSinks.containsKey(key);
		final SinkInfo updateNode = isNew ? node : mSinks.get(key);
		if (isNew) {
			updateNode.update(node);
		} else {
			mSinks.put(key, updateNode);
		}

    	synchronized(mSinkEventListeners) {
    		for (final SinkEventListener l : mSinkEventListeners) {
    			new Thread() {
    				public void run() {
    					l.onSinkUpdated(PulseManager.this, updateNode);
    				}
    			}.start();
    		}
    	}
    }

    private void onSinkRemoved(final int index) {
    	synchronized(mSinkEventListeners) {
    		for (final SinkEventListener l : mSinkEventListeners) {
    			new Thread() {
    				public void run() {
    					l.onSinkRemoved(PulseManager.this, index);
    				}
    			}.start();
    		}
    	}
    }

    public void addOnSinkInputEventListener(SinkInputEventListener l) {
    	synchronized(mSinkInputEventListeners) {
    		mSinkInputEventListeners.add(l);
    	}
	}
	public void removeOnSinkInputEventListener(SinkInputEventListener l) {
		synchronized(mSinkInputEventListeners) {
			mSinkInputEventListeners.remove(l);
		}
	}
	
	private void onSinkInputUpdated(final SinkInput node) {
		final Integer key = new Integer(node.getIndex());
		final boolean isNew = !mSinkInputs.containsKey(key);
		final SinkInput updateNode = isNew ? node : mSinkInputs.get(key);
		final boolean ownerChanged = isNew || 
				(updateNode.getOwnerIndex() == node.getOwnerIndex());
		if (isNew) {
			updateNode.update(node);
		} else {
			mSinkInputs.put(key, updateNode);
		}
		
		if (ownerChanged) {
			updateNode.setOwner(getSink(updateNode.getOwnerIndex(), true));
		}

		synchronized(mSinkInputEventListeners) {
			for (final SinkInputEventListener l : mSinkInputEventListeners) {
				new Thread() {
					public void run() {
						l.onSinkInputUpdated(PulseManager.this, updateNode);
					}
				}.start();
			}
		}
	}
	
	private void onSinkInputRemoved(final int index) {
		mSinkInputs.remove(new Integer(index));
		
		synchronized(mSinkInputEventListeners) {
			for (final SinkInputEventListener l : mSinkInputEventListeners) {
				new Thread() {
					public void run() {
						l.onSinkInputRemoved(PulseManager.this, index);
					}
				}.start();
			}
		}
	}
	
	public void addOnSourceOutputEventListener(SourceOutputEventListener l) {
		synchronized(mSourceOutputEventListeners) {
			mSourceOutputEventListeners.add(l);
		}
	}
	public void removeOnSourceOutputEventListener(SourceOutputEventListener l) {
		synchronized(mSourceOutputEventListeners) {
			mSourceOutputEventListeners.remove(l);
		}
	}
	
	private void onSourceOutputUpdated(final SourceOutput node) {
		final Integer key = new Integer(node.getIndex());
		final boolean isNew = !mSourceOutputs.containsKey(key);
		final SourceOutput updateNode = isNew ? node : mSourceOutputs.get(key);
		final boolean ownerChanged = isNew || 
				(updateNode.getOwnerIndex() == node.getOwnerIndex());
		if (isNew) {
			updateNode.update(node);
		} else {
			mSourceOutputs.put(key, updateNode);
		}
		
		// When sources are implemented
		if (ownerChanged) {
//			updateNode.setOwner(getSink(updateNode.getOwnerIndex(), true));
		}
		
		synchronized(mSourceOutputEventListeners) {
			for (final SourceOutputEventListener l : mSourceOutputEventListeners) {
				new Thread() {
					public void run() {
						l.onSourceOutputUpdated(PulseManager.this, updateNode);
					}
				}.start();
			}
		}
	}
	
	private void onSourceOutputRemoved(final int index) {
		synchronized(mSourceOutputEventListeners) {
			for (final SourceOutputEventListener l : mSourceOutputEventListeners) {
				new Thread() {
					public void run() {
						l.onSourceOutputRemoved(PulseManager.this, index);
					}
				}.start();
			}
		}
	}

	public void addOnPulseConnectionListener(PulseConnectionListener l) {
		synchronized(mPulseConnectionListeners) {
			mPulseConnectionListeners.add(l);
		}
	}
	public void removeOnPulseConnectionListener(PulseConnectionListener l) {
		synchronized(mPulseConnectionListeners) {
			mPulseConnectionListeners.remove(l);
		}
	}
	
	private void onPulseConnectionReady() {
		// Get all of the stream nodes
		mPulse.getSinkInfoList(new SinkInfoCallback() {
			@Override
			public void run(SinkInfo node) {
				onSinkUpdated(node);
			}
		});
		
		mPulse.getSinkInputInfoList(new SinkInputInfoCallback() {
			@Override
			public void run(SinkInput node) {
				onSinkInputUpdated(node);
			}
		});
		
		mPulse.getSourceOutputInfoList(new SourceOutputInfoCallback() {
			@Override
			public void run(SourceOutput node) {
				onSourceOutputUpdated(node);
			}
		});
		
		// Alert the listeners.
		synchronized(mPulseConnectionListeners) {
			for (final PulseConnectionListener l : mPulseConnectionListeners) {
				l.onPulseConnectionReady(PulseManager.this);
			}
		}
	}
	
	private void onPulseConnectionFailed() {
		synchronized(mPulseConnectionListeners) {
			for (final PulseConnectionListener l : mPulseConnectionListeners) {
				l.onPulseConnectionFailed(PulseManager.this);
			}
		}
	}
	public Map<Integer, SinkInfo> getSinks() {
		return mSinks;
	}
	public SinkInfo getSink(int i) { return getSink(i, false); }
	protected SinkInfo getSink(int i, boolean putStub) {
		SinkInfo sink = mSinks.get(new Integer(i));
		if (sink != null) {
			return sink;
		} else if (putStub) {
			sink = new SinkInfo(mPulse);
			mSinks.put(new Integer(i), 	sink);
			return sink;
		} else {
			return null;
		}
	}
	
	public Map<Integer, SinkInput> getSinkInputs() {
		return mSinkInputs;
	}
	public Map<Integer, SourceOutput> getSourceOutputs() {
		return mSourceOutputs;
	}
}
