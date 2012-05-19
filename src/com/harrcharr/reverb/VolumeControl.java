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
package com.harrcharr.reverb;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.harrcharr.pulse.StreamNode;
import com.harrcharr.pulse.Volume;

public class VolumeControl extends LinearLayout {
	protected StreamNode mNode;
	protected Volume mVolume; // The volume which this layout represents
	protected ArrayList<VolumeSlider> mSliders;
	
	public boolean ignoreSet;
	
	public VolumeControl(Context context) {
		super(context);
		setOrientation(VERTICAL);
	}
	public VolumeControl(Context context, AttributeSet attr) {
		super(context, attr);
		setOrientation(VERTICAL);
	}
	public VolumeControl(Context context, AttributeSet attr, int arg) {
		super(context, attr, arg);
		setOrientation(VERTICAL);
	}
	public VolumeControl(Context context, Volume volume) {
		this(context);
		setVolume(volume);
	}
	
	public synchronized void setNode(StreamNode node) {
		mNode = node;
		
		setVolume(mNode.getVolume());
	}
	
	public synchronized void setVolume(Volume volume) {	
		mVolume = volume; 
		Log.d("Reverb", "Num channels "+volume.getNumChannels());
		if (mSliders == null) {			
			mSliders = new ArrayList<VolumeSlider>();
			int i = 0;
			for (int chVol : volume.getVolumes()) {
				VolumeSlider v = new VolumeSlider(getContext(), chVol, i); 
				
				mSliders.add(v);
				this.addView(v);
				Log.d("Reverb", "Addin a volume");
				i++;
			}
		} else if (volume.getNumChannels() > mSliders.size()) {
			int oldMax = mSliders.size();
			for (int i = oldMax; i < volume.getNumChannels(); i++) {
				Log.d("Reverb", "case 1");
				VolumeSlider v = new VolumeSlider(getContext(), volume.getVolumes()[i], i); 
				
				mSliders.add(v);
				this.addView(v);
			}
			for (int i = 0; i < oldMax; i++) {
				Log.d("Reverb", "case 2");
				mSliders.get(i).setVolume(volume.getVolumes()[i]);
			}
		} else if (volume.getNumChannels() < mSliders.size()) {
			Log.d("Reverb", "case 3");
			for (int i = mSliders.size()-1; i > volume.getNumChannels(); i--) {
				this.removeView(mSliders.remove(i));
			}
			for (int i = 0; i < mSliders.size(); i++) {
				mSliders.get(i).setVolume(volume.getVolumes()[i]);
			}
		} else {
			Log.d("Reverb", "case 4");
			for (int i = 0; i < mSliders.size(); i++) {
				mSliders.get(i).setVolume(volume.getVolumes()[i]);
			}
		}
	}
	
	protected void volumeChanged(int channel, int volume) {
		mVolume.changeVolume(channel, volume);
		mNode.setVolume(mVolume, null);
	}
	
	protected class VolumeSlider extends RelativeLayout
	implements SeekBar.OnSeekBarChangeListener{
		protected TextView mChannelName;
		protected SeekBar mVolumeSlider;
		protected TextView mLinear;
		protected TextView mDb;
		
		protected int mChannel;
		
		private boolean ignoreSet;
		
		public VolumeSlider(Context context, int volume, int channel) {
			super(context);
			View.inflate(context, R.layout.volume_slider, this);
			
			mChannel = channel;
			
			mVolumeSlider = (SeekBar)this.findViewById(R.id.volumeSlider);
			mVolumeSlider.setOnSeekBarChangeListener(this);
			
			ignoreSet = false;
			
			setVolume(volume);
			Log.d("Reverb", "Settin' volume");
		}
		
		public void setVolume(int volume) {
			mVolumeSlider.setProgress(volume);
		}
		
		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
		    super.onLayout(changed, l, t, r, b);
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, final int progress,
				boolean fromUser) {
			if (fromUser) {
				new Thread(new Runnable() {
					public void run() {
						VolumeControl.this.volumeChanged(mChannel, progress);
					}
				}).run();				
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			Log.d("Reverb", "Starting touch tracking");
			ignoreSet = true;
			VolumeControl.this.ignoreSet = true;
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			Log.d("Reverb", "Stopping touch tracking");
			ignoreSet = false;
		}
	}
}
