package com.harrcharr.reverb.pulseutil;

import com.harrcharr.pulse.SourceInfo;

public interface SourceEventListener {
	public void onSourceUpdated(PulseManager p, SourceInfo node);
	public void onSourceRemoved(PulseManager p, int index);
}
