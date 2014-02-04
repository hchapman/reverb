package com.harrcharr.reverb.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.harrcharr.pulse.StreamNode;
import com.harrcharr.reverb.R;

public class OwnerStreamNodeView<Node extends StreamNode> extends StreamNodeView<Node> {
	public OwnerStreamNodeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public OwnerStreamNodeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OwnerStreamNodeView(Context context) {
		super(context);
	}

	protected void inflateViewFromLayout(Context context) {
		View.inflate(context, R.layout.owner_node_view, this);
	}
}
