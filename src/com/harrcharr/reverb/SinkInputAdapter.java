package com.harrcharr.reverb;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.harrcharr.reverb.pulse.InfoCallback;
import com.harrcharr.reverb.pulse.PulseContext;
import com.harrcharr.reverb.pulse.SinkInput;
import com.harrcharr.reverb.pulse.SubscriptionCallback;
import com.harrcharr.reverb.pulse.Volume;

public class SinkInputAdapter extends StreamNodeAdapter<SinkInput> {
	public SinkInputAdapter(Activity context, PulseContext pulse) {
		super(context, pulse);
		
		mInfoCall = new SinkInputCallback(this);
		mSubscriptionCall = new SinkInputSubscriptionCallback(this);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("Reverb", "Getting View "+position);
		
		ViewHolder holder;
		
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
        	//convertView = new PlayView(mContext);
            convertView = mInflater.inflate(R.layout.node_list_item, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        holder.loadNodeIntoViews((SinkInput)getItem(position));

        return convertView;
	}
	
	private class ViewHolder {
		private SinkInput mNode;
		
        private TextView nodeName;
        private SeekBar nodeVolume;
        private ToggleButton nodeMute;
        
        public ViewHolder(View convertView) {
            nodeName = (TextView) convertView.findViewById(R.id.nodeName);
            nodeVolume = (SeekBar) convertView.findViewById(R.id.nodeVolume);
            nodeMute = (ToggleButton) convertView.findViewById(R.id.nodeMute);
            
            nodeVolume.setMax(Volume.NORM);
            
            convertView.setTag(this);
        }
        
        public void loadNodeIntoViews(SinkInput node) {
        	mNode = node;
        	nodeName.setText(node.getName());
            nodeVolume.setProgress(mNode.getVolume().get());
        	
        	nodeMute.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mNode.setMute(((ToggleButton)v).isChecked(), null);
				}
			});
        }
    }
	
	private static class SinkInputCallback implements InfoCallback<SinkInput> {
		SinkInputAdapter mAdapter; // SinkInputAdapter to update
		
		public SinkInputCallback(SinkInputAdapter adapter) {
			mAdapter = adapter;
			Log.d("Reverb [adapter]", "We're initializing a SICB.");
		}
		public void run(int idx, long iPtr) {
			Log.d("Reverb [adapter]", "We're in a SinkInputCallback run().");
			
			if (mAdapter.hasIndex(idx)) {
				mAdapter.getItem(idx).update(iPtr);
			}
			else {
				mAdapter.addNode(new SinkInput(mAdapter.getPulseContext(), iPtr));
			}
		}
	}
	
	private static class SinkInputSubscriptionCallback implements SubscriptionCallback {
		SinkInputAdapter mAdapter;
		
		public SinkInputSubscriptionCallback(SinkInputAdapter adapter) {
			mAdapter = adapter;
		}
		
		public void run(int type, int index) {
			Log.d("Reverb", type + " " + index);
			if (type == 32) { // Change this lolol actually definitely :/
				mAdapter.removeNode(index);
			} else {
				mAdapter.getPulseContext()
					.getSinkInputInfo(index, mAdapter.getInfoCallback());
			}
		}
	}
}
