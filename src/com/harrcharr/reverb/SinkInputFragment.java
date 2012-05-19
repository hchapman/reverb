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
import java.util.Iterator;
import java.util.Map.Entry;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.harrcharr.reverb.pulse.InfoCallback;
import com.harrcharr.reverb.pulse.PulseContext;
import com.harrcharr.reverb.pulse.SinkInput;
import com.harrcharr.reverb.pulse.SinkInputInfoCallback;
import com.harrcharr.reverb.pulse.SubscriptionCallback;

public class SinkInputFragment extends SherlockFragment {
	protected HashMap<Integer, SinkInput> mNodes;
	protected PulseContext mPulse;
	
	protected InfoCallback<SinkInput> mInfoCallback;
	protected SubscriptionCallback mSubscriptionCallback;
	
	protected ViewGroup mNodeHolder;
	
    public SinkInputFragment() {
		super();
		
		mInfoCallback = new SinkInputCallback();
		mSubscriptionCallback = new SinkInputSubscriptionCallback();
	}
    
    public synchronized void setPulseContext(PulseContext pulse) {
    	mPulse = pulse;
    	
    	// Now that we've changed our PulseContext, we have to reinstantiate.
    	mNodes = new HashMap<Integer, SinkInput>();
    	
    	Log.e("Reverb", "Context set, activity is "+getActivity()+"Added? "+isAdded());
    	Log.e("Reverb", getActivity()+" is our activity");
    	
		mPulse.subscribeSinkInput(getSubscriptionCallback());
		mPulse.getSinkInputInfoList(getInfoCallback());
   
    	if (isVisible()) {
	    	getActivity().runOnUiThread(new Runnable() {
				public void run() {
					try {
					getViewGroup().removeAllViews();
					Log.d("Reverb", "Removed stale nodes");
					} catch (Exception e) {
						
					}
					
				}
			});
    	}
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sink_input_fragment, container, false);
        
        mNodeHolder = (ViewGroup)v.findViewById(R.id.nodeHolder);
        Iterator<Entry<Integer, SinkInput>> nodeIterator = 
        		mNodes.entrySet().iterator();
        
        while (nodeIterator.hasNext()) {
        	Log.e("Reverb", "We're adding something");
        	final StreamNodeView<SinkInput> nodeView = 
        			new StreamNodeView<SinkInput>(getActivity());
        	nodeView.setNode(nodeIterator.next().getValue());
        	
        	mNodeHolder.addView(nodeView);
        }
        
        TextView w = new TextView(getActivity());
        w.setText("Frig frig frig");
        mNodeHolder.addView(w);
        
        Log.e("Reverb", "mNodeHolder is "+mNodeHolder);
        
        return v;
    }
    
    protected void addNode(SinkInput node) {
    	mNodes.put(new Integer(node.getIndex()), node);
    	
    	if(getViewGroup() != null) {
    		final StreamNodeView<SinkInput> nodeView = 
    				new StreamNodeView<SinkInput>(getActivity());
    		nodeView.setNode(node);
    	
    		Log.e("Reverb", "We want to add a node");
    		getActivity().runOnUiThread(new Runnable(){
    			public void run() {
    				mNodeHolder.addView(nodeView);
    			}
    		});	
    	}
    }
    protected void removeNode(int index) {
    	final SinkInput node = mNodes.remove(index);
    	
    	if(getViewGroup() != null) {
	    	getActivity().runOnUiThread(new Runnable(){
	    		public void run() {
	    	    	mNodeHolder.removeView(mNodeHolder.findViewById(node.getIndex()));
	    		}
	    	});	
    	}
    }
    
    protected ViewGroup getViewGroup() {
    	if (getView() == null)
    		return null;
    	
    	return (ViewGroup)getView().findViewById(R.id.nodeHolder);
    }
    
    protected StreamNodeView<SinkInput> getStreamNodeViewByIndex(int idx) {
    	if (getViewGroup() == null)
    		return null;
    				
    	return (StreamNodeView<SinkInput>)getViewGroup().findViewById(idx);
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
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("Reverb", "Created. mPulse is "+mPulse+" activity is "+getActivity());
		Log.e("Reverb", "Context set, activity is "+getActivity()+"Added? "+isAdded());
		
		setPulseContext(((ReverbActivity)getActivity()).getPulseContext());
	}
    
	private class SinkInputCallback extends SinkInputInfoCallback {
		public void run(final SinkInput si) {
			int idx = si.getIndex();
			Log.d("Reverb [adapter]", "We're in a SinkInputCallback run().");
			Log.d("Reverb", "Update index "+idx+"view group"+getViewGroup());
			Log.e("Reverb", "mNodeHolder is "+SinkInputFragment.this.mNodeHolder);
			
			if (getViewGroup() != null) {
				final StreamNodeView<SinkInput> v = getStreamNodeViewByIndex(si.getIndex());	
				
				Log.d("Reverb", "Update node is "+v+" and index "+idx);
				if (v != null) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							v.setNode(si);
						}
					});
					
					Log.d("Reverb", "put with idx "+idx);
					return;
				}
			}
			
			Log.d("Reverb", "put with idx "+idx);
			SinkInputFragment.this.addNode(si);
		}
	}
	
	private class SinkInputSubscriptionCallback extends SubscriptionCallback {
		public void run(int type, int index) {
			Log.d("Reverb", type + " " + index);
			if (type == EVENT_REMOVE) {
				removeNode(index);
			} else {
				Log.w("Reverb", ""+getPulseContext());
				getPulseContext().getSinkInputInfo(index, getInfoCallback());
			}
		}
	}

}
