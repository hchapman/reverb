package com.harrcharr.reverb.pulseutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.harrcharr.pulse.Mainloop;
import com.harrcharr.pulse.NoConnectionException;
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
	protected Mainloop m;
	protected PulseContext mPulse;
	protected List<PulseConnectionListener> mPulseConnectionListeners =
			new ArrayList<PulseConnectionListener>();	
	
	protected List<SinkEventListener> mSinkEventListeners =
			new ArrayList<SinkEventListener>();
	protected List<SinkInputEventListener> mSinkInputEventListeners =
			new ArrayList<SinkInputEventListener>();
	protected List<SourceOutputEventListener> mSourceOutputEventListeners =
			new ArrayList<SourceOutputEventListener>();

	
	private String mServer;
	
	protected HashMap<Integer, SinkInfo> mSinks = new HashMap<Integer, SinkInfo>();
	//protected HashMap<Integer, SourceInfo> mSources;
	protected HashMap<Integer, SinkInput> mSinkInputs = new HashMap<Integer, SinkInput>();
	protected HashMap<Integer, SourceOutput> mSourceOutputs = new HashMap<Integer, SourceOutput>();
	
	public PulseManager() {
		m = new Mainloop();
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
    	
    	mPulse = new PulseContext(m);

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
		mSinkEventListeners.add(l);
	}
	public void removeOnSinkEventListener(SinkEventListener l) {
		mSinkEventListeners.remove(l);
	}
	
	private void onSinkUpdated(final SinkInfo node) {
		mSinks.put(new Integer(node.getIndex()), node);
		
		for (final SinkEventListener l : mSinkEventListeners) {
			new Thread() {
				public void run() {
					l.onSinkUpdated(PulseManager.this, node);
				}
			}.start();
		}
	}
	
	private void onSinkRemoved(final int index) {
		for (final SinkEventListener l : mSinkEventListeners) {
			new Thread() {
				public void run() {
					l.onSinkRemoved(PulseManager.this, index);
				}
			}.start();
		}
	}
	
	public void addOnSinkInputEventListener(SinkInputEventListener l) {
		mSinkInputEventListeners.add(l);
	}
	public void removeOnSinkInputEventListener(SinkInputEventListener l) {
		mSinkInputEventListeners.remove(l);
	}
	
	private void onSinkInputUpdated(final SinkInput node) {
		mSinkInputs.put(new Integer(node.getIndex()), node);
		
		for (final SinkInputEventListener l : mSinkInputEventListeners) {
			new Thread() {
				public void run() {
					l.onSinkInputUpdated(PulseManager.this, node);
				}
			}.start();
		}
	}
	
	private void onSinkInputRemoved(final int index) {
		for (final SinkInputEventListener l : mSinkInputEventListeners) {
			new Thread() {
				public void run() {
					l.onSinkInputRemoved(PulseManager.this, index);
				}
			}.start();
		}
	}
	
	public void addOnSourceOutputEventListener(SourceOutputEventListener l) {
		mSourceOutputEventListeners.add(l);
	}
	public void removeOnSourceOutputEventListener(SourceOutputEventListener l) {
		mSourceOutputEventListeners.remove(l);
	}
	
	private void onSourceOutputUpdated(final SourceOutput node) {
		mSourceOutputs.put(new Integer(node.getIndex()), node);
		
		for (final SourceOutputEventListener l : mSourceOutputEventListeners) {
			new Thread() {
				public void run() {
					l.onSourceOutputUpdated(PulseManager.this, node);
				}
			}.start();
		}
	}
	
	private void onSourceOutputRemoved(final int index) {
		for (final SourceOutputEventListener l : mSourceOutputEventListeners) {
			new Thread() {
				public void run() {
					l.onSourceOutputRemoved(PulseManager.this, index);
				}
			}.start();
		}
	}

	public void addOnPulseConnectionListener(PulseConnectionListener l) {
		mPulseConnectionListeners.add(l);
	}
	public void removeOnPulseConnectionListener(PulseConnectionListener l) {
		mPulseConnectionListeners.remove(l);
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
		for (final PulseConnectionListener l : mPulseConnectionListeners) {
			new Thread() {
				public void run() {
					l.onPulseConnectionReady(PulseManager.this);
				}
			}.start();
		}
	}
	
	private void onPulseConnectionFailed() {
		for (final PulseConnectionListener l : mPulseConnectionListeners) {
			new Thread() {
				public void run() {
					l.onPulseConnectionFailed(PulseManager.this);
				}
			}.start();
		}
	}
	
	public Map<Integer, SinkInfo> getSinks() {
		return mSinks;
	}
	public Map<Integer, SinkInput> getSinkInputs() {
		return mSinkInputs;
	}
	public Map<Integer, SourceOutput> getSourceOutputs() {
		return mSourceOutputs;
	}
}
