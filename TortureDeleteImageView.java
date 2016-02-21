package com.isayyo.app.editimg;

import com.avos.avoscloud.LogUtil.log;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TortureDeleteImageView extends ImageView {

	public TortureDeleteImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public TortureDeleteImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TortureDeleteImageView(Context context) {
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
			TortureImageViewActivity.list.remove(frameLayout.getChildAt(4));
		}
		log.e("Deleted a emoji", "a" + TortureImageViewActivity.have_select_emoji);
		TortureImageViewActivity.have_select_emoji--;
		return super.onTouchEvent(event);
	}
}
