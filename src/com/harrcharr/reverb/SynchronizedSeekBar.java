package com.harrcharr.reverb;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SynchronizedSeekBar extends SeekBar {
	private OnTouchEventListener mTouchEventListener;
	
	public interface OnTouchEventListener {
		public boolean onSeekTouchEvent(MotionEvent event);
	}

	public SynchronizedSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SynchronizedSeekBar(Context context) {
		super(context);
	}

	public SynchronizedSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setOnTouchEventListener(OnTouchEventListener listener) {
		mTouchEventListener = listener;
	}
	
	public boolean sendTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		boolean retVal = super.onTouchEvent(event);
		if (mTouchEventListener != null)
			mTouchEventListener.onSeekTouchEvent(event);
		return retVal;
	}
}
