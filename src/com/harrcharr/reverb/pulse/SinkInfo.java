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
	public void setVolume(Volume volume, SuccessCallback cb) {
	
	}
	
	public native Volume getVolume();
	
	private final native void JNIUpdateWithInfo(long pSinkInfo);

	@Override
	public String getDescriptiveName() {
		// TODO Auto-generated method stub
		return null;
	}
}
