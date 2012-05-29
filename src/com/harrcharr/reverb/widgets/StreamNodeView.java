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
package com.harrcharr.reverb.widgets;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.harrcharr.pulse.StreamNode;
import com.harrcharr.pulse.Volume;
import com.harrcharr.reverb.R;
import com.harrcharr.reverb.R.id;
import com.harrcharr.reverb.R.layout;
import com.harrcharr.reverb.widgets.SynchronizedSeekBar.OnTouchEventListener;

public class StreamNodeView<Node extends StreamNode> extends RelativeLayout {
	protected Node mNode;
	
	protected TextView mName;
	
	protected Volume mVolume; // The volume which this layout represents
	protected ArrayList<VolumeSlider> mSliders;
	
	protected ToggleButton mMute, mLocked;	
	protected ViewGroup mVolumeGroup;
	
	private boolean mTracking;
	
	public StreamNodeView(Context context) {
		super(context);
		inflateViewFromLayout(context);
		prepareViews();
	}

	public StreamNodeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflateViewFromLayout(context);
		prepareViews();
	}
	
	public StreamNodeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inflateViewFromLayout(context);
		prepareViews();
	}
	
	protected void inflateViewFromLayout(Context context) {
		View.inflate(context, R.layout.owned_node_view, this);
	}
	
	protected void prepareViews() {
		mName = (TextView) this.findViewById(R.id.nodeName);
		
		mVolumeGroup = (ViewGroup) this.findViewById(R.id.volumeHolder);
		
        mMute = (ToggleButton) this.findViewById(R.id.nodeMute);
        mLocked = (ToggleButton) this.findViewById(R.id.lockChannels);
        
    	mMute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mNode.setMute(((ToggleButton)v).isChecked(), null);
			}
		});

	}
	
	protected void reload() {
    	mName.setText(mNode.getDescriptiveName() + " " + mNode.getIndex());
        
		setMute(mNode.isMuted());
		setVolume(mNode.getVolume());
	}
	
	public void setNode(Node node) {
		mNode = node;
		setId(node.getIndex());
		reload();
	}
	
	public void update(long ptr) {
		
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	    super.onLayout(changed, l, t, r, b);
	}
	
	public synchronized void setVolume(Volume volume) {	
		mVolume = volume; 
		
		// If we presently have no volume sliders in the widget
		if (mSliders == null) {			
			mSliders = new ArrayList<VolumeSlider>();
			int i = 0;
			for (int chVol : volume.getVolumes()) {
				VolumeSlider v = new VolumeSlider(getContext(), chVol, i); 
				
				mSliders.add(v);
				mVolumeGroup.addView(v);
				i++;
			}
			
		// If we have fewer sliders in the widget than we have channels in the new volume
		} else if (volume.getNumChannels() > mSliders.size()) {
			int oldMax = mSliders.size();
			for (int i = oldMax; i < volume.getNumChannels(); i++) {
				VolumeSlider v = new VolumeSlider(getContext(), volume.getVolumes()[i], i); 
				
				mSliders.add(v);
				mVolumeGroup.addView(v);
			}
			for (int i = 0; i < oldMax; i++) {
				if (shouldUpdateChannel(i))
					mSliders.get(i).setVolume(volume.getVolumes()[i]);
			}
			
		// If we have more sliders in the widget than channels in the new volume
		} else if (volume.getNumChannels() < mSliders.size()) {
			for (int i = mSliders.size()-1; i > volume.getNumChannels(); i--) {
				mVolumeGroup.removeView(mSliders.remove(i));
			}
			for (int i = 0; i < mSliders.size(); i++) {
				if (shouldUpdateChannel(i))
					mSliders.get(i).setVolume(volume.getVolumes()[i]);
			}
			
		// We have the same number of sliders in the volume as in the widget.
		// This is the most common case.
		} else {
			for (int i = 0; i < mSliders.size(); i++) {
				if (shouldUpdateChannel(i))
					mSliders.get(i).setVolume(volume.getVolumes()[i]);
			}
		}
	}
	
	public boolean channelsLocked() {
		return mLocked.isChecked();
	}
	
	private boolean shouldUpdateChannel(int channel) {
		return !(mSliders.get(channel).isTracking() || 
				(channelsLocked() && mTracking));
	}
	
	protected void volumeChanged(int channel, int volume) {
		if (channelsLocked()) {
			mVolume.setVolume(volume);
			for (VolumeSlider slider : mSliders) {
				if (!slider.isTracking())
					slider.setVolume(volume);
			}
		} else {
			mVolume.setVolume(channel, volume);
		}
		mNode.setVolume(mVolume, null);
	}
	
	public void dispatchMotionEvent(int channel, MotionEvent event) {
		if (channelsLocked()) {
			for (VolumeSlider slider : mSliders) {
				slider.dispatchMotionEvent(channel, event);
			}
		}
	}
	
	protected void setMute(boolean muted) {
		mMute.setChecked(muted);
	}
	
	protected class VolumeSlider extends RelativeLayout
	implements SeekBar.OnSeekBarChangeListener, OnTouchEventListener {
		protected TextView mChannelName;
		protected SynchronizedSeekBar mVolumeSlider;
		protected TextView mLinear;
		protected TextView mDb;
		
		protected int mChannel;
		
		private boolean mTracking;
		
		public VolumeSlider(Context context, int volume, int channel) {
			super(context);
			View.inflate(context, R.layout.volume_slider, this);
			
			mChannel = channel;
			
			mVolumeSlider = (SynchronizedSeekBar)this.findViewById(R.id.volumeSlider);
			mVolumeSlider.setOnSeekBarChangeListener(this);
			mVolumeSlider.setOnTouchEventListener(this);
			
			mLinear = (TextView)this.findViewById(R.id.linearValue);
			mDb = (TextView)this.findViewById(R.id.dbValue);
			
			mTracking = false;
			
			setVolume(volume);
		}
		
		public void setVolume(int volume) {
			setVolume(volume, true);
		}
		
		public void setVolume(int volume, boolean updateSlider) {
			if (updateSlider)
				mVolumeSlider.setProgress(volume);
			mLinear.setText(" (" + Volume.asPercent(volume, 1) + "%)");
			
			double db = Volume.asDecibels(volume, 2);
			mDb.setText((volume == 0 ? "-\u221E" : db) + " dB");
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
						StreamNodeView.this.volumeChanged(mChannel, progress);
					}
				}).run();				
				setVolume(progress, false);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mTracking = true;
			StreamNodeView.this.mTracking = true;
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mTracking = false;
			StreamNodeView.this.mTracking = false;
		}
		
		public boolean isTracking() {
			return mTracking;
		}

		public boolean onSeekTouchEvent(MotionEvent event) {
			StreamNodeView.this.dispatchMotionEvent(mChannel, event);
			return true;
		}

		public boolean dispatchMotionEvent(int channel, MotionEvent event) {
			if (mChannel != channel) {
				return mVolumeSlider.sendTouchEvent(event);
			}
			
			return false;
		}
	}
}
