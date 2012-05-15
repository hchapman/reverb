package com.harrcharr.reverb.pulse;

public abstract class SinkInputInfoCallback extends InfoCallback<SinkInput> {	
	@Override
	public final void run(long ptr) {
		run(new SinkInput(mPulse, ptr));
	}

	public abstract void run(final SinkInput node);
}
