package com.harrcharr.reverb.pulse;

public interface SubscriptionCallback {
	public final static int EVENT_REMOVE = 32;
	public void run(int event, int index);
}
