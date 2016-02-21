package com.isayyo.app.editimg;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class DeleteImageView extends ImageView {

	public DeleteImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public DeleteImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public DeleteImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		FrameLayout frameLayout = (FrameLayout)getParent();
		FrameLayout layout = (FrameLayout)frameLayout.getParent();
		layout.removeView(frameLayout);
		if (frameLayout.getChildCount() >= 5) {
			TouchImageViewActivity.list.remove(frameLayout.getChildAt(4));
		}
		return super.onTouchEvent(event);
	}
}
