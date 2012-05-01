package com.harrcharr.reverb.pulse;

import android.util.Log;

public class Volume {
	public static int NORM = 0x10000;
	public static int MUTED = 0;
	
	char mChannels;
	int[] mVolumes;
	
	public Volume(int[] vols) {
		mVolumes = vols;
		mChannels = (char)vols.length;
	}
	
	public int[] getVolumes() {
		return mVolumes;
	}
	public int getNumChannels() {
		return mVolumes.length;
	}
	public int get() {
		return mVolumes[0];
	}
//	public Volume(char channels, int[] values) {
//		// init it somehow maybe
//	}
	
	public final native int getMax();
}
