package com.harrcharr.reverb;

import java.util.Map;

import android.util.Log;

import com.harrcharr.pulse.InfoCallback;
import com.harrcharr.pulse.SinkInput;
import com.harrcharr.pulse.SinkInputInfoCallback;
import com.harrcharr.pulse.SubscriptionCallback;
import com.harrcharr.reverb.pulseutil.PulseManager;
import com.harrcharr.reverb.pulseutil.SinkInputEventListener;
import com.harrcharr.reverb.widgets.StreamNodeView;

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
	public Map<Integer, SinkInput> getNodesFromManager(PulseManager p) {
		return p.getSinkInputs();
	}

	@Override
	public void onManagerAttached(PulseManager p) {
		p.addOnSinkInputEventListener(this);
	}

}
