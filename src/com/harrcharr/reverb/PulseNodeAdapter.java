package com.harrcharr.reverb;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.harrcharr.reverb.pulse.JNIStruct;
import com.harrcharr.reverb.pulse.PulseContext;

public abstract class PulseNodeAdapter extends BaseAdapter {
	private List<? extends JNIStruct> mNodes;
	protected LayoutInflater mInflater;
	protected Context mContext;
	protected PulseContext mPulse;
	
	public PulseNodeAdapter(Context context, PulseContext pulse, List<? extends JNIStruct> nodes) {
		mNodes = nodes;
		mPulse = pulse;
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mNodes.size();
	}

	@Override
	public JNIStruct getItem(int position) {
		return mNodes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);
}
