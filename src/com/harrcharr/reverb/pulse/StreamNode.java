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
