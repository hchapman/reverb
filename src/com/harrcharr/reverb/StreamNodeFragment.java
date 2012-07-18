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

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.harrcharr.pulse.StreamNode;
import com.harrcharr.reverb.pulseutil.HasPulseManager;
import com.harrcharr.reverb.pulseutil.PulseConnectionListener;
import com.harrcharr.reverb.pulseutil.PulseManager;
import com.harrcharr.reverb.widgets.StreamNodeView;

public abstract class StreamNodeFragment<T extends StreamNode> extends SherlockFragment
implements PulseConnectionListener, HasPulseManager, SharedPreferences.OnSharedPreferenceChangeListener {
	protected ViewGroup mNodeHolder;
	private MarginLayoutParams mLayoutParams;
	private ArrayList<StreamNodeView<T>> mNodeViews = new ArrayList<StreamNodeView<T>>();
	
	public StreamNodeFragment() {
	}

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.stream_node_fragment, container, false);
        mNodeHolder = (ViewGroup)v.findViewById(R.id.nodeHolder);
        
        PulseManager p = (getActivity() == null ? null : 
        	((HasPulseManager)getActivity()).getPulseManager());
        
        if (p != null) {
        	synchronized(p) {
				SparseArray<T> nodes = getNodesFromManager(p);
				for (int i = 0; i < nodes.size(); i++) {
					T node = nodes.get(nodes.keyAt(i));
					if (node != null) {
						updateNode(node);
					}
		    	}
        	}
        }
        
        ReverbSharedPreferences.registerOnSharedPreferenceChangeListener(getActivity(), this);
        
        return v;
    }
    
    protected void updateNode(final T node) {
    	if(getViewGroup() != null) {
    		StreamNodeView<T> v = getStreamNodeViewByIndex(node.getIndex());
    		
    		FragmentActivity activity = getActivity();
    		
    		if(activity == null)
				return;
    		
    		if (v == null) {    			
    			activity.runOnUiThread(new Runnable() {
    				public void run() {
    					final StreamNodeView<T> nodeView = makeNewStreamNodeView();

    					setNewNode(nodeView, node);
    				}
    			});
    		} else {
    			final StreamNodeView<T> nodeView = v;
    			
    			activity.runOnUiThread(new Runnable() {
    				public void run() {
    					setNode(nodeView, node);
    				}
    			});
    		}	
    	}
    }
    protected void setNewNode(StreamNodeView<T> nodeView, final T node) {
    	if (mLayoutParams == null) {
    		mLayoutParams = new LinearLayout.LayoutParams(getViewGroup().getLayoutParams());
    		mLayoutParams.setMargins(0, 0, 0, 10);
    	}
    	
    	getViewGroup().addView(nodeView, mLayoutParams);
    	mNodeViews.add(nodeView);
    	nodeView.setNode(node);
    }
    protected void setNode(StreamNodeView<T> nodeView, final T node) {
    	nodeView.setNode(node);
    }
    protected void removeNode(final int index) {
    	if(getViewGroup() != null) {
    		final StreamNodeView<T> v = getStreamNodeViewByIndex(index);
    		if (v != null) {
    			getActivity().runOnUiThread(new Runnable(){
    				public void run() {
    					getViewGroup().removeView(v);
    				}
    			});
    			
    			// Destroy the StreamNode, and anything it might be holding on to
    			mNodeViews.remove(v);
    			v.disconnect();
    		}
    	}
    }
    
    protected ViewGroup getViewGroup() {
    	return mNodeHolder;
    }
    
    protected StreamNodeView<T> getStreamNodeViewByIndex(int idx) {
    	if (getViewGroup() == null)
    		return null;
    				
    	try {
    		return (StreamNodeView<T>)getViewGroup().findViewById(idx);
    	} catch (ClassCastException e) {
    		return null;
    	}
    } 
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			PulseManager p = ((HasPulseManager)activity).getPulseManager();
			p.addOnPulseConnectionListener(this);
			onManagerAttached(p);
		} catch (ClassCastException e) {
			Log.e("StreamNodeFragment", "Activity to which this fragment " +
					"is attached must have a PulseManager");
			throw(new Error("A stream node fragment must be attached to" +
					"an activity with a pulsemanager."));
		}
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		for (StreamNodeView<T> v : mNodeViews) {
			v.disconnect();
		}
		mNodeViews = new ArrayList<StreamNodeView<T>>();
		ReverbSharedPreferences.unregisterOnSharedPreferenceChangeListener(getActivity(), this);
	}
	
	public abstract void onManagerAttached(PulseManager p);
	public abstract SparseArray<T> getNodesFromManager(PulseManager p);
	
	protected abstract StreamNodeView<T> makeNewStreamNodeView();
    
	public void onPulseConnectionReady(final PulseManager p) {
    	if (getActivity() != null) {
	    	getActivity().runOnUiThread(new Runnable() {
				public void run() {
					try {
						getViewGroup().removeAllViews();
						Log.d("Reverb", "Removed stale nodes");
					} catch (Exception e) {
						Log.e("Reverb", e.getMessage());
					}
					
					synchronized(p) {
						SparseArray<T> nodes = getNodesFromManager(p);
						for (int i = 0; i < nodes.size(); i++) {
							T node = nodes.get(nodes.keyAt(i));
							if (node != null) {
								updateNode(node);
							}
				    	}
					}
				}
			});
	    	

    	}
		
	}
	public void onPulseConnectionFailed(PulseManager p) {
		// Right now we do nothing on failed connect.
	}
	
	public PulseManager getPulseManager() {
		return ((HasPulseManager)getActivity()).getPulseManager();
	}

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if(key.equals(getActivity().getString(R.string.prefs_key_display_vol_peaks))) {
			synchronized(mNodeViews) {
				for (StreamNodeView nodeView : mNodeViews) {
					nodeView.setDisplayPeaks(ReverbSharedPreferences.displayPeaks(getActivity()));
				}
			}
		}
	}
}
