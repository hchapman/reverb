package com.harrcharr.reverb.pulse;

public class ClientInfo extends PulseNode {
	String sName;
	
	public ClientInfo(PulseContext pulse, long ptr) {
		super(pulse, ptr);
	}
	
	public void update(long ptr) {
		JNIPopulateStruct(ptr);
	}
	public String getName() {
		return sName;
	}
	
	public String toString() {
		return sName;
	}
	
	private final native void JNIPopulateStruct(long pClientInfo);

	@Override
	public String getDescriptiveName() {
		return sName;
	}
}
