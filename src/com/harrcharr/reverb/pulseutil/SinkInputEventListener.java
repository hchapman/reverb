package com.harrcharr.reverb.pulseutil;

import com.harrcharr.pulse.SinkInput;

public interface SinkInputEventListener {
	public void onSinkInputUpdated(PulseManager p, SinkInput node);
	public void onSinkInputRemoved(PulseManager p, int index);
}
