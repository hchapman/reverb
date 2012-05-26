package com.harrcharr.reverb.pulseutil;

public interface PulseConnectionListener {
	public void onPulseConnectionReady(PulseManager p);
	public void onPulseConnectionFailed(PulseManager p);
}
 