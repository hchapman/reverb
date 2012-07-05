package com.harrcharr.reverb.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.harrcharr.pulse.OwnedStreamNode;
import com.harrcharr.pulse.StreamNode;
import com.harrcharr.reverb.R;

public class OwnedStreamNodeView<Node extends OwnedStreamNode> extends StreamNodeView<Node> {
	private TextView mOwnerName;
	public OwnedStreamNodeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public OwnedStreamNodeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OwnedStreamNodeView(Context context) {
		super(context);
	}

	protected void inflateViewFromLayout(Context context) {
		View.inflate(context, R.layout.owned_node_view, this);
	}
	
	@Override
	protected void prepareViews() {
		super.prepareViews();
		
		//mOwnerName = (TextView)findViewById(R.id.ownerName);
	}
	
	@Override
	protected void reload() {
		super.reload();
		
		final StreamNode owner = mNode.getOwner();
		//mOwnerName.setText(owner == null ? "Unknown" : owner.getDescriptiveName());
	}
}
