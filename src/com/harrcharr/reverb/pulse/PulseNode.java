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

import java.nio.ByteBuffer;

public abstract class PulseNode extends JNIStruct {
	public static String PROP_APPLICATION_NAME = "application.name";
	
	protected PulseContext mPulse;

	protected int mIndex;
	protected int mOwnerModule;
	
	protected String mName;
	protected String mDriver;
	
	public PulseNode(PulseContext pulse, long iPtr) {
		super(iPtr);
		mPulse = pulse;
	}
	
	public int getIndex() {
		return mIndex;
	}
	
	/*
	 * Returns a human-readable name for this PulseNode.
	 */
	public abstract String getDescriptiveName();
}
