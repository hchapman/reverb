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

import com.harrcharr.reverb.pulse.Volume;

public class VolumeControl extends LinearLayout {
	protected Volume mVolume; // The volume which this layout represents
	protected ArrayList<VolumeSlider> mSliders;
	
	public VolumeControl(Context context) {
		super(context);
	}
	public VolumeControl(Context context, AttributeSet attr) {
		super(context, attr);
	}
	public VolumeControl(Context context, AttributeSet attr, int arg) {
		super(context, attr, arg);
	}
	public VolumeControl(Context context, Volume volume) {
		this(context);
		setVolume(volume);
	}
	
	public synchronized void setVolume(Volume volume) {
		if (mSliders == null) {			
			mSliders = new ArrayList<VolumeSlider>();
			for (int chVol : volume.getVolumes()) {
				VolumeSlider v = new VolumeSlider(getContext(), chVol); 
				
				mSliders.add(v);
				this.addView(v);
			}
		}
		else if (volume.getNumChannels() > mSliders.size()) {
			int oldMax = mSliders.size();
			for (int i = oldMax; i < volume.getNumChannels(); i++) {
				VolumeSlider v = new VolumeSlider(getContext(), volume.getVolumes()[i]); 
				
				mSliders.add(v);
				this.addView(v);
			}
			for (int i = 0; i < oldMax; i++) {
				mSliders.get(i).setVolume(volume.getVolumes()[i]);
			}
		} else if (volume.getNumChannels() < mSliders.size()) {
			for (int i = mSliders.size()-1; i > volume.getNumChannels(); i--) {
				this.removeView(mSliders.remove(i));
			}
			for (int i = 0; i < mSliders.size(); i++) {
				mSliders.get(i).setVolume(volume.getVolumes()[i]);
			}
		} else {
			for (int i = 0; i < mSliders.size(); i++) {
				mSliders.get(i).setVolume(volume.getVolumes()[i]);
			}
		}
	}
	
	protected class VolumeSlider extends RelativeLayout {
		protected TextView mChannelName;
		protected SeekBar mVolumeSlider;
		protected TextView mLinear;
		protected TextView mDb;
		
		public VolumeSlider(Context context, int volume) {
			super(context);
			View.inflate(context, R.layout.volume_slider, this);
			
			mVolumeSlider = (SeekBar)this.findViewById(R.id.volumeSlider);
			
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
	}
}
