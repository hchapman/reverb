package com.harrcharr.reverb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.harrcharr.reverb.pulse.InfoCallback;
import com.harrcharr.reverb.pulse.PulseContext;
import com.harrcharr.reverb.pulse.StreamNode;
import com.harrcharr.reverb.pulse.SubscriptionCallback;

public abstract class StreamNodeAdapter<T extends StreamNode> extends BaseAdapter {
	private Map<Integer, T> mNodes;
	private List<Integer> mIndices;
	
	protected LayoutInflater mInflater;
	protected Activity mContext;
	protected PulseContext mPulse;
	
	protected InfoCallback<T> mInfoCall;
	protected SubscriptionCallback mSubscriptionCall;
	
	public StreamNodeAdapter(Activity context, PulseContext pulse) {
		mNodes = new HashMap<Integer, T>();
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mPulse = pulse;
		update();	
	}
	
	protected void addNode(T node) {
		mNodes.put(new Integer(node.getIndex()), node);
		update();
	}
	protected void removeNode(int idx) { removeNode(new Integer(idx)); }
	protected void removeNode(Integer idx) {
		mNodes.remove(idx);
		update();
	}
	public T getNode(int idx) { return getNode(new Integer(idx)); }
	public T getNode(Integer idx) {
		return mNodes.get(idx);
	}
	
	public boolean hasIndex(int idx) { return hasIndex(new Integer(idx)); }
	public boolean hasIndex(Integer idx) {
		return mNodes.containsKey(idx);
	}
	
	private void update() {
		mIndices = sortedKeyList(mNodes.keySet());
		mContext.runOnUiThread(new Runnable() {
			public void run() {
				//StreamNodeAdapter.this.notifyDataSetChanged();
			}
		});
	}
	
	/*
	 * Get this Adapter's update callback.
	 */
	public InfoCallback<T> getInfoCallback() {
		return mInfoCall;
	}
	public SubscriptionCallback getSubscriptionCallback() {
		return mSubscriptionCall;
	}
	
	/*
	 * Returns this adapters associated PA context
	 */
	public PulseContext getPulseContext() {
		return mPulse;
	}
	
	@Override
	public int getCount() {
		return mNodes.size();
	}

	@Override
	public T getItem(int position) {
		Log.d("Reverb", "Our indices are "+mIndices);
		return mNodes.get(mIndices.get(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);
	
	protected static <I extends Comparable<? super I>> List<I> sortedKeyList(Collection<I> c) {
	  List<I> list = new ArrayList<I>(c);
	  java.util.Collections.sort(list);
	  return list;
	}
}
