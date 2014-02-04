package com.harrcharr.reverb.widgets;

import com.actionbarsherlock.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.CompoundButton;

public class CenterCompoundButton extends CompoundButton {
	private Drawable mButtonDrawable;
	
	public CenterCompoundButton(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public CenterCompoundButton(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.buttonStyle);
	}

	public CenterCompoundButton(Context context) {
		this(context, null);
	}
	
	@Override
    public void setButtonDrawable(Drawable d) {
        if (d != null) {
            if (mButtonDrawable != null) {
                mButtonDrawable.setCallback(null);
                unscheduleDrawable(mButtonDrawable);
            }
            d.setCallback(this);
            d.setState(getDrawableState());
            d.setVisible(getVisibility() == VISIBLE, false);
            mButtonDrawable = d;
            mButtonDrawable.setState(null);
            setMinHeight(mButtonDrawable.getIntrinsicHeight());
        }

        refreshDrawableState();
    }

	
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final Drawable buttonDrawable = mButtonDrawable;
        if (buttonDrawable != null) {
        	final int verticalGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
        	final int horizontalGravity = getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK;
            final int height = buttonDrawable.getIntrinsicHeight();
            final int width = buttonDrawable.getIntrinsicWidth();

            int x = 0;
            int y = 0;

            switch (verticalGravity) {
                case Gravity.BOTTOM:
                    y = getHeight() - height;
                    break;
                case Gravity.CENTER_VERTICAL:
                    y = (getHeight() - height) / 2;
                    break;
            }
            
            switch (horizontalGravity) {
            case Gravity.RIGHT:
                x = getWidth() - width;
                break;
            case Gravity.CENTER_HORIZONTAL:
                x = (getWidth() - width) / 2;
                break;
        }

            buttonDrawable.setBounds(x, y, x + width, y + height);
            buttonDrawable.draw(canvas);
        }
    }	
    
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        
        if (mButtonDrawable != null) {
            int[] myDrawableState = getDrawableState();
            
            // Set the state of the Drawable
            mButtonDrawable.setState(myDrawableState);
            
            invalidate();
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == mButtonDrawable;
    }
}
