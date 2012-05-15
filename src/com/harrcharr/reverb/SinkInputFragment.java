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
import com.harrcharr.reverb.pulse.SinkInputInfoCallback;
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
    	
    	// Now that we've changed our PulseContext, we have to reinstantiate.
    	mNodes = new HashMap<Integer, StreamNodeView<SinkInput>>();
    	getActivity().runOnUiThread(new Runnable() {
			public void run() {
				getViewGroup().removeAllViews();
				Log.d("Reverb", "Removed stale nodes");
			}
		});
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
    
	private class SinkInputCallback extends SinkInputInfoCallback {
		public void run(final SinkInput si) {
			int idx = si.getIndex();
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
