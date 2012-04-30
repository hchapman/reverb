package com.harrcharr.reverb.pulse;

public class SinkInfo extends StreamNode {
	String sName;
	String sDescription;
	
	public SinkInfo(PulseContext pulse, long ptr) {
		super(pulse, ptr);
	}
	
	public void update(long ptr) {
		JNIUpdateWithInfo(ptr);
	}
	public String getDescription() {
		return sDescription;
	}
	
	public String toString() {
		return sName + "\n" + sDescription;
	}
	
	public void setMute(boolean mute, SuccessCallback cb) {
		
	}
	
	public native Volume getVolume();
	
	private final native void JNIUpdateWithInfo(long pSinkInfo);

	@Override
	public String getDescriptiveName() {
		// TODO Auto-generated method stub
		return null;
	}
}
