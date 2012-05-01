package com.harrcharr.reverb.pulse;

public abstract class StreamNode extends PulseNode {
	protected boolean mMuted;
	protected boolean mCorked;
	
	protected Volume mVolume;
	
	public StreamNode(PulseContext pulse, long iPtr) {
		super(pulse, iPtr);
	}
	
	public Volume getVolume() {
		return mVolume;
	}
	
	public abstract void setMute(boolean mute, SuccessCallback c);
	public abstract void setVolume(Volume volume, SuccessCallback c);
}
