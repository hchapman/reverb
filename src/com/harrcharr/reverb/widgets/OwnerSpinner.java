package com.harrcharr.reverb.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Spinner;

public class OwnerSpinner extends Spinner {
	public OwnerSpinner(Context context, AttributeSet attrs, int defStyle,
			int mode) {
		super(context, attrs, defStyle, mode);
	}

	public OwnerSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public OwnerSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OwnerSpinner(Context context, int mode) {
		super(context, mode);
	}

	public OwnerSpinner(Context context) {
		super(context);
	}
	
	public void setSelectionByIndex(int index) {
		if (getAdapter() == null)
			return;
		
		final int position = ((OwnerStreamsAdapter)getAdapter()).getPosition(index);
		
		setSelection(position, true);
		Log.d("OwnerSpinner", "Set selection to "+position);
	}

}
