package com.harrcharr.reverb;

import com.harrcharr.pulse.PulseContext;

public interface PulseInterface {
	public PulseContext getPulseContext();
	public void registerPulseListener(Runnable runnable);
	public void unregisterPulseListener();
}
