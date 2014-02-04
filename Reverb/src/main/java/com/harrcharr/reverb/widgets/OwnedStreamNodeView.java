package com.harrcharr.reverb.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.harrcharr.pulse.OwnedStreamNode;
import com.harrcharr.pulse.OwnerStreamNode;
import com.harrcharr.pulse.SuccessCallback;
import com.harrcharr.reverb.R;

public class OwnedStreamNodeView<Node extends OwnedStreamNode> extends StreamNodeView<Node>
	implements OwnerSpinner.OnItemSelectedListener {
	private OwnerSpinner mOwnerSelector;
	private boolean mOwnerLoading;
	private int mOwnerIndex = 0;
	
	private boolean mViewAcceptsInput = false;
	
	public void setViewAcceptsInput(boolean viewAcceptsInput) {
		this.mViewAcceptsInput = viewAcceptsInput;
	}

	private TextView mAppName;
	
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
		
		mOwnerSelector = (OwnerSpinner)this.findViewById(R.id.ownerSelector);

		synchronized(mOwnerSelector) {
			mOwnerLoading = true;
			mOwnerSelector.setOnItemSelectedListener(this);
			mOwnerLoading = false;
		}
	}
	
	@Override
	protected void prepareViews() {
		super.prepareViews();
		
		mAppName = (TextView) this.findViewById(R.id.nodeApplication);
	}
	
	public void setSelectorAdapter(OwnerStreamsAdapter adapter) {
		synchronized(mOwnerSelector) {
			mOwnerLoading = true;
			mOwnerSelector.setAdapter(adapter);
			mOwnerLoading = false;
		}
		Log.d("OwnedStreamNodeView", "Set the selector adapter again");
	}
	
	@Override
	protected void reload() {
		super.reload();
		
		mAppName.setText(mNode.getAppName());
		mOwnerIndex = mNode.getOwnerIndex();
		Log.i("OwnedStreamNodeView", "Owner index set to "+mOwnerIndex);

		mOwnerSelector.setSelectionByIndex(mOwnerIndex);
	}

	@Override
	public void onItemSelected(AdapterView<?> spinner, View view, int position, 
			long id) {
		if (!mViewAcceptsInput) {
			mViewAcceptsInput = true;
			return;
		}
		
		OwnerStreamNode owner = ((OwnerStreamNode)spinner.getItemAtPosition(position));
		if (mOwnerIndex != owner.getIndex()) {
			Log.i("OwnedStreamNodeView", "Item selected. Changing owner to "+owner.getIndex());
			Log.i("OwnedStreamNodeView", "Old index was "+mOwnerIndex);
			mNode.moveNode(owner, null);
			
			mOwnerIndex = owner.getIndex();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}
