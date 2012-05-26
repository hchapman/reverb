package com.harrcharr.reverb.pulseutil;

import com.harrcharr.pulse.SourceOutput;

public interface SourceOutputEventListener {
	public void onSourceOutputUpdated(PulseManager p, SourceOutput node);
	public void onSourceOutputRemoved(PulseManager p, int index);
}
