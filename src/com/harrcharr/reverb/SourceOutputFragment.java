package com.harrcharr.reverb;

import java.util.Map;

import android.util.Log;

import com.harrcharr.pulse.InfoCallback;
import com.harrcharr.pulse.SinkInput;
import com.harrcharr.pulse.SourceOutput;
import com.harrcharr.pulse.SourceOutputInfoCallback;
import com.harrcharr.pulse.SubscriptionCallback;
import com.harrcharr.reverb.pulseutil.PulseManager;
import com.harrcharr.reverb.pulseutil.SourceOutputEventListener;
import com.harrcharr.reverb.widgets.StreamNodeView;

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
	public Map<Integer, SourceOutput> getNodesFromManager(PulseManager p) {
		return p.getSourceOutputs();
	}

	@Override
	public void onManagerAttached(PulseManager p) {
		p.addOnSourceOutputEventListener(this);
	}
}
