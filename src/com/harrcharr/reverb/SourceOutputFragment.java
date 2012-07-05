package com.harrcharr.reverb;

import android.util.SparseArray;

import com.harrcharr.pulse.SourceOutput;
import com.harrcharr.reverb.pulseutil.PulseManager;
import com.harrcharr.reverb.pulseutil.SourceOutputEventListener;
import com.harrcharr.reverb.widgets.OwnedStreamNodeView;

public class SourceOutputFragment extends StreamNodeFragment<SourceOutput>
implements SourceOutputEventListener{
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
	public SparseArray<SourceOutput> getNodesFromManager(PulseManager p) {
		return p.getSourceOutputs();
	}

	@Override
	public void onManagerAttached(PulseManager p) {
		p.addOnSourceOutputEventListener(this);
	}
	
	@Override
	protected OwnedStreamNodeView<SourceOutput> makeNewStreamNodeView() {
		return new OwnedStreamNodeView<SourceOutput>(getActivity());
	}
}
