/*******************************************************************************
 * Copyright (c) 2012 Harrison Chapman.
 * 
 * This file is part of Reverb.
 * 
 *     Reverb is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 2 of the License, or
 *     (at your option) any later version.
 * 
 *     Reverb is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Reverb.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Harrison Chapman - initial API and implementation
 ******************************************************************************/
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
	
	public void changeVolume(int channel, int volume) {
		mVolumes[channel] = volume;
	}
//	public Volume(char channels, int[] values) {
//		// init it somehow maybe
//	}
	
	public final native int getMax();
}
