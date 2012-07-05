package com.harrcharr.reverb;

import android.util.SparseArray;

import com.harrcharr.pulse.SinkInput;
import com.harrcharr.reverb.pulseutil.PulseManager;
import com.harrcharr.reverb.pulseutil.SinkInputEventListener;
import com.harrcharr.reverb.widgets.OwnedStreamNodeView;

public class SinkInputFragment extends StreamNodeFragment<SinkInput>
implements SinkInputEventListener {
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
	public SparseArray<SinkInput> getNodesFromManager(PulseManager p) {
		return p.getSinkInputs();
	}

	@Override
	public void onManagerAttached(PulseManager p) {
		p.addOnSinkInputEventListener(this);
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
