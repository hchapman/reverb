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

import com.actionbarsherlock.app.SherlockFragment;
import com.harrcharr.pulse.InfoCallback;
import com.harrcharr.pulse.PulseContext;
import com.harrcharr.pulse.StreamNode;
import com.harrcharr.pulse.SubscriptionCallback;

public abstract class StreamNodeFragment<T extends StreamNode> extends SherlockFragment {
	protected HashMap<Integer, T> mNodes;
	protected PulseContext mPulse;
	
	protected ViewGroup mNodeHolder;
	
    public StreamNodeFragment() {
		super();
	}
    
    public synchronized void setPulseContext(PulseContext pulse) {
    	mPulse = pulse;
    	
    	// Now that we've changed our PulseContext, we have to reinstantiate.
    	mNodes = new HashMap<Integer, T>();
    	
    	Log.e("Reverb", "Context set, activity is "+getActivity()+"Added? "+isAdded());
    	Log.e("Reverb", getActivity()+" is our activity");
    	
		mPulse.subscribeSinkInput(getSubscriptionCallback());
		
		subscribeStreamNode();
		loadStreamNodeList();
   
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
        
        if (mNodes == null)
        	return v;
        
        Iterator<Entry<Integer, T>> nodeIterator = 
        		mNodes.entrySet().iterator();
        
        while (nodeIterator.hasNext()) {
        	Log.e("Reverb", "We're adding something");
        	final StreamNodeView<T> nodeView = 
        			new StreamNodeView<T>(getActivity());
        	nodeView.setNode(nodeIterator.next().getValue());
        	
        	getViewGroup().addView(nodeView);
        }
        
        return v;
    }
    
    protected void addNode(T node) {
    	mNodes.put(new Integer(node.getIndex()), node);
    	
    	if(getViewGroup() != null) {
    		final StreamNodeView<T> nodeView = 
    				new StreamNodeView<T>(getActivity());
    		nodeView.setNode(node);
    	
    		Log.e("Reverb", "We want to add a node");
    		getActivity().runOnUiThread(new Runnable(){
    			public void run() {
    				getViewGroup().addView(nodeView);
    			}
    		});	
    	}
    }
    protected void removeNode(int index) {
    	final T node = mNodes.remove(index);
    	if (node == null)
    		return;
    	
    	if(getViewGroup() != null) {
	    	getActivity().runOnUiThread(new Runnable(){
	    		public void run() {
	    			getViewGroup()
	    				.removeView(getStreamNodeViewByIndex(node.getIndex()));
	    		}
	    	});	
    	}
    }
    
    protected abstract void loadStreamNodeList();
    protected abstract void subscribeStreamNode();
    
    protected ViewGroup getViewGroup() {
    	if (getView() == null)
    		return null;
    	
    	return (ViewGroup)getView().findViewById(R.id.nodeHolder);
    }
    
    protected StreamNodeView<T> getStreamNodeViewByIndex(int idx) {
    	if (getViewGroup() == null)
    		return null;
    				
    	return (StreamNodeView<T>)getViewGroup().findViewById(idx);
    }
    
    protected PulseContext getPulseContext() {
    	return mPulse;
    }
    
	protected abstract InfoCallback<T> getInfoCallback();
	protected abstract SubscriptionCallback getSubscriptionCallback();
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		final PulseInterface p = (PulseInterface)activity;
		p.registerPulseListener(new Runnable(){
			public void run() {
				StreamNodeFragment.this.setPulseContext(p.getPulseContext());
			}
		});
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
    

}
