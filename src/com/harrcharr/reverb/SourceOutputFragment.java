package com.harrcharr.reverb;

import android.util.SparseArray;

import com.harrcharr.pulse.SourceOutput;
import com.harrcharr.reverb.pulseutil.PulseManager;
import com.harrcharr.reverb.pulseutil.SourceOutputEventListener;
import com.harrcharr.reverb.widgets.OwnedStreamNodeView;
import com.harrcharr.reverb.widgets.OwnerStreamsAdapter;
import com.harrcharr.reverb.widgets.StreamNodeView;

public class SourceOutputFragment extends StreamNodeFragment<SourceOutput>
implements SourceOutputEventListener{
	OwnerStreamsAdapter mSourcesAdapter;
	
	public SourceOutputFragment() {
		super(); 
	}
	
	@Override
	public void onSourceOutputUpdated(PulseManager p, SourceOutput node) {
		updateNode(node);
	}

	@Override
	public void onSourceOutputRemoved(PulseManager p, int index) {
		removeNode(index);
	} 

	@Override
	protected void setNewNode(StreamNodeView<SourceOutput> nodeView, final SourceOutput node) {
		super.setNewNode(nodeView, node);
		((OwnedStreamNodeView<SourceOutput>)nodeView).setSelectorAdapter(mSourcesAdapter);
	}
	
	@Override
	public SparseArray<SourceOutput> getNodesFromManager(PulseManager p) {
		return p.getSourceOutputs();
	}

	@Override
	public void onManagerAttached(PulseManager p) {
		p.addOnSourceOutputEventListener(this);
		mSourcesAdapter = new OwnerStreamsAdapter(p.getSources());
	}
	
	@Override
	protected OwnedStreamNodeView<SourceOutput> makeNewStreamNodeView() {
		return new OwnedStreamNodeView<SourceOutput>(getActivity());
	}
}
