package com.harrcharr.reverb;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.harrcharr.reverb.pulse.PulseContext;
import com.harrcharr.reverb.pulse.SinkInput;

public class SinkInputAdapter extends PulseNodeAdapter {
	public SinkInputAdapter(Context context, PulseContext pulse, List<SinkInput> nodes) {
		super(context, pulse, nodes);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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
            
            convertView.setTag(this);
        }
        
        public void loadNodeIntoViews(SinkInput node) {
        	mNode = node;
        	nodeName.setText(node.getName());
        	
        	nodeMute.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mNode.setMute(((ToggleButton)v).isChecked(), null);
				}
			});
        }
    }
}
