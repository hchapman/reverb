package com.harrcharr.reverb;

import java.util.Map;

import android.util.Log;

import com.harrcharr.pulse.SinkInfo;
import com.harrcharr.reverb.pulseutil.PulseManager;
import com.harrcharr.reverb.pulseutil.SinkEventListener;
import com.harrcharr.reverb.widgets.OwnerStreamNodeView;
import com.harrcharr.reverb.widgets.StreamNodeView;

public class SinkFragment extends StreamNodeFragment<SinkInfo>
implements SinkEventListener {
	public SinkFragment() {
		super();
	}

	@Override
	public void onSinkUpdated(PulseManager p, SinkInfo node) {
		updateNode(node);
	}

	@Override
	public void onSinkRemoved(PulseManager p, int index) {
		removeNode(index);
	}

	@Override
	public Map<Integer, SinkInfo> getNodesFromManager(PulseManager p) {
		return p.getSinks();
	}
	
	@Override
	public void onManagerAttached(PulseManager p) {
		p.addOnSinkEventListener(this);
	}
	
	@Override
	protected StreamNodeView<SinkInfo> makeNewStreamNodeView() {
		return new OwnerStreamNodeView<SinkInfo>(getActivity());
	}
}
