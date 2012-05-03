package com.harrcharr.reverb;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.harrcharr.reverb.pulse.StreamNode;

public class StreamNodeView<Node extends StreamNode> extends LinearLayout {
	protected Node mNode;
	
	protected TextView mName;
	protected VolumeControl mVolumeControl;
	protected ToggleButton mMute;
	
	public StreamNodeView(Context context) {
		super(context);
		View.inflate(context, R.layout.node_view, this);
		initInterface();
	}

	public StreamNodeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		View.inflate(context, R.layout.node_view, this);
		initInterface();
	}

	public StreamNodeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.node_view, this);
		initInterface();
	}
	
	protected void initInterface() {
		mName = (TextView) this.findViewById(R.id.nodeName);
        mVolumeControl = (VolumeControl) this.findViewById(R.id.nodeVolume);
        mMute = (ToggleButton) this.findViewById(R.id.nodeMute);
        
    	mMute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mNode.setMute(((ToggleButton)v).isChecked(), null);
			}
		});
	}
	
	protected void reload() {
    	mName.setText(mNode.getDescriptiveName());
        mVolumeControl.setNode(mNode);
	}
	
	public void setNode(Node node) {
		mNode = node;
		reload();
	}
	
	public void update(long ptr) {
		
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	    super.onLayout(changed, l, t, r, b);
	}
}
