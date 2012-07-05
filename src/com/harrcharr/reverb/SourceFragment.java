package com.harrcharr.reverb;

import android.util.SparseArray;

import com.harrcharr.pulse.SinkInfo;
import com.harrcharr.pulse.SourceInfo;
import com.harrcharr.reverb.pulseutil.PulseManager;
import com.harrcharr.reverb.pulseutil.SourceEventListener;
import com.harrcharr.reverb.widgets.OwnerStreamNodeView;
import com.harrcharr.reverb.widgets.StreamNodeView;

public class SourceFragment extends StreamNodeFragment<SourceInfo>
implements SourceEventListener {
	public SourceFragment() {
		super();
	}

	@Override
	public void onSourceUpdated(PulseManager p, SourceInfo node) {
		updateNode(node);
	}

	@Override
	public void onSourceRemoved(PulseManager p, int index) {
		removeNode(index);
	}

	@Override
	public SparseArray<SourceInfo> getNodesFromManager(PulseManager p) {
		return p.getSources();
	}
	
	@Override
	public void onManagerAttached(PulseManager p) {
		p.addOnSourceEventListener(this);
	}
	
	@Override
	protected StreamNodeView<SourceInfo> makeNewStreamNodeView() {
		return new OwnerStreamNodeView<SourceInfo>(getActivity());
	}
}
