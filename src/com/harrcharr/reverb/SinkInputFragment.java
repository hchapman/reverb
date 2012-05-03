package com.harrcharr.reverb;

import java.util.HashMap;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harrcharr.reverb.pulse.InfoCallback;
import com.harrcharr.reverb.pulse.PulseContext;
import com.harrcharr.reverb.pulse.SinkInput;
import com.harrcharr.reverb.pulse.SubscriptionCallback;

public class SinkInputFragment extends Fragment {
	protected HashMap<Integer, StreamNodeView<SinkInput>> mNodes;
	protected PulseContext mPulse;
	
	protected InfoCallback<SinkInput> mInfoCallback;
	protected SubscriptionCallback mSubscriptionCallback;
	
    public SinkInputFragment() {
		super();
		
		mNodes = new HashMap<Integer, StreamNodeView<SinkInput>>();
		
		mInfoCallback = new SinkInputCallback();
		mSubscriptionCallback = new SinkInputSubscriptionCallback();
	}
    
    public void setPulseContext(PulseContext pulse) {
    	mPulse = pulse;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sink_input_fragment, container, false);
    }
    
    protected void addNode(SinkInput node) {
    	final StreamNodeView<SinkInput>v = new StreamNodeView<SinkInput>(getActivity());
    	v.setNode(node);
    	mNodes.put(new Integer(node.getIndex()), v);
    	
    	getActivity().runOnUiThread(new Runnable(){
    		public void run() {
    	    	getViewGroup().addView(v);
    		}
    	});	
    }
    protected void removeNode(int index) {
    	final View v = mNodes.remove(index);
    	
    	getActivity().runOnUiThread(new Runnable(){
    		public void run() {
    	    	getViewGroup().removeView(v);
    		}
    	});	
    }
    
    protected ViewGroup getViewGroup() {
    	return (ViewGroup)getView().findViewById(R.id.nodeHolder);
    }
    
    protected PulseContext getPulseContext() {
    	return mPulse;
    }
    
	protected InfoCallback<SinkInput> getInfoCallback() {
		return mInfoCallback;
	}
	protected SubscriptionCallback getSubscriptionCallback() {
		return mSubscriptionCallback;
	}
    
	private class SinkInputCallback implements InfoCallback<SinkInput> {
		public void run(int idx, long iPtr) {
			final SinkInput si = new SinkInput(SinkInputFragment.this.getPulseContext(), iPtr);
			Log.d("Reverb [adapter]", "We're in a SinkInputCallback run().");
			Log.d("Reverb", "Update index "+idx);
			final StreamNodeView<SinkInput> v = SinkInputFragment.this.mNodes.get(si.getIndex());
			Log.d("Reverb", "Update node is "+v+" and index "+idx);
			if (v != null) {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						v.setNode(si);
					}
				});
				
				Log.d("Reverb", "put with idx "+idx);
			}
			else {
				Log.d("Reverb", "put with idx "+idx);
				SinkInputFragment.this.addNode(si);
			}
		}
	}
	
	private class SinkInputSubscriptionCallback implements SubscriptionCallback {
		public SinkInputSubscriptionCallback() {
		}
		
		public void run(int type, int index) {
			Log.d("Reverb", type + " " + index);
			if (type == EVENT_REMOVE) {
				SinkInputFragment.this.removeNode(index);
			} else {
				SinkInputFragment.this.getPulseContext()
					.getSinkInputInfo(index, SinkInputFragment.this.getInfoCallback());
			}
		}
	}

}
