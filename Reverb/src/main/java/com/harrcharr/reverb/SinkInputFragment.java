package com.harrcharr.reverb;

import android.os.Bundle;
import android.util.SparseArray;

import com.harrcharr.pulse.SinkInput;
import com.harrcharr.reverb.pulseutil.PulseManager;
import com.harrcharr.reverb.pulseutil.SinkInputEventListener;
import com.harrcharr.reverb.widgets.OwnedStreamNodeView;
import com.harrcharr.reverb.widgets.OwnerStreamsAdapter;
import com.harrcharr.reverb.widgets.StreamNodeView;

public class SinkInputFragment extends StreamNodeFragment<SinkInput>
implements SinkInputEventListener {
	OwnerStreamsAdapter mSinksAdapter;
	
	public SinkInputFragment() {
		super();
	}

	@Override
	public void onSinkInputUpdated(PulseManager p, SinkInput node) {
		updateNode(node);
	}

	@Override
	public void onSinkInputRemoved(PulseManager p, int index) {
		removeNode(index);
	}
	
	@Override
	protected void setNewNode(StreamNodeView<SinkInput> nodeView, final SinkInput node) {
		super.setNewNode(nodeView, node);
		((OwnedStreamNodeView<SinkInput>)nodeView).setSelectorAdapter(mSinksAdapter);
	}

	@Override
	public SparseArray<SinkInput> getNodesFromManager(PulseManager p) {
		return p.getSinkInputs();
	}

	@Override
	public void onManagerAttached(PulseManager p) {
		p.addOnSinkInputEventListener(this);
		mSinksAdapter = new OwnerStreamsAdapter(p.getSinks());
	}
	
	@Override
	protected OwnedStreamNodeView<SinkInput> makeNewStreamNodeView() {
		return new OwnedStreamNodeView<SinkInput>(getActivity());
	}
//
//	@Override
//	protected StreamNodeView<SinkInput> updateNode(SinkInput node) {
//		StreamNodeView<SinkInput> v = super.updateNode(node);
//		
//		return v;
//	}
}
