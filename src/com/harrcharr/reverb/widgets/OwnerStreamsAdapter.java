package com.harrcharr.reverb.widgets;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.harrcharr.pulse.OwnerStreamNode;

public class OwnerStreamsAdapter extends BaseAdapter {
	SparseArray<? extends OwnerStreamNode> mArray;
	
	public OwnerStreamsAdapter(SparseArray<? extends OwnerStreamNode> sparseArray) {
		mArray = sparseArray;
	}
	
	@Override
	public int getCount() {
		return mArray.size();
	}

	@Override
	public Object getItem(int position) {
		return mArray.valueAt(position);
	}

	@Override
	public long getItemId(int position) {
		return mArray.keyAt(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getViewWithResource(position, convertView, parent, 
				android.R.layout.simple_spinner_item);
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getViewWithResource(position, convertView, parent, 
				android.R.layout.simple_spinner_dropdown_item);
	}
	
	private View getViewWithResource(int position, View convertView, ViewGroup parent, int resource) {
		View view;
		TextView text;
		
		if (convertView == null) {
			view = LayoutInflater.from(parent.getContext()).inflate(resource, 
					parent, false);
		} else {
			view = convertView;
		}
		
        try {
        	text = (TextView) view;
        } catch (ClassCastException e) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }
		
		text.setText(((OwnerStreamNode)getItem(position)).getDescription());
		
		return view;
	}

}
