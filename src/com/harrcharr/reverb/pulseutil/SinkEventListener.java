package com.harrcharr.reverb.pulseutil;

import com.harrcharr.pulse.SinkInfo;

public interface SinkEventListener {
	public void onSinkUpdated(PulseManager p, SinkInfo node);
	public void onSinkRemoved(PulseManager p, int index);
}
