package com.harrcharr.reverb.pulse;

public interface InfoCallback<T extends PulseNode> {
	//public void run_from_ptr(long iPtr);
	public void run(int idx, long iPtr);
}
