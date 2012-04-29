package com.harrcharr.reverb.pulse;

public abstract class PulseNode extends JNIStruct {
	protected PulseContext mPulse;
	
	public PulseNode(PulseContext pulse, long iPtr) {
		super(iPtr);
		mPulse = pulse;
	}
	
	public abstract void setMute(boolean mute, SuccessCallback c);
}
