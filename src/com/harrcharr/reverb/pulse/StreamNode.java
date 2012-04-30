package com.harrcharr.reverb.pulse;

public abstract class StreamNode extends PulseNode {
	protected boolean mMuted;
	protected boolean mCorked;
	protected Volume mVolume;
	
	public StreamNode(PulseContext pulse, long iPtr) {
		super(pulse, iPtr);
	}
	
	public abstract void setMute(boolean mute, SuccessCallback c);
}
