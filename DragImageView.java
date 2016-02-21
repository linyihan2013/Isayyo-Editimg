/*
 * TouchImageView.java
 * By: Michael Ortiz
 * Updated By: Patrick Lackemacher
 * -------------------
 * Extends Android ImageView to include pinch zooming and panning.
 */

package com.isayyo.app.editimg;

import android.R.integer;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

public class DragImageView extends ImageView {
	
	int width = 0;
	int height = 0;
	
	int layout_w = 0;
	int layout_h = 0;
	
	int x_down = 0;
	int y_down = 0;
	int x_cha = 0;
	int y_cha = 0;
	
	int x_new = 0;
	int y_new = 0;
	
	int x = 0;
	int y = 0;
	
	int left = 0;
	int top = 0;
	int right = 0;
	int bottom = 0;
	
	FrameLayout layout;
	LayoutParams layoutParams;

	public DragImageView(Context context) {
		super(context);
		width = TouchImageViewActivity.content.getWidth();
		height = TouchImageViewActivity.content.getHeight();
		
	}

	/**
	 * 该构造方法在静态引入XML文件中是必须的
	 * 
	 * @param context
	 * @param paramAttributeSet
	 */
	public DragImageView(Context context, AttributeSet paramAttributeSet) {
		super(context, paramAttributeSet);
		width = TouchImageViewActivity.content.getWidth();
		height = TouchImageViewActivity.content.getHeight();
		
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				x_down = (int)event.getRawX();
				y_down = (int)event.getRawY();
				layout = (FrameLayout)this.getParent();
				layoutParams = (LayoutParams)layout.getLayoutParams();
				layout_w = layout.getWidth();
				layout_h = layout.getHeight();
				left = layoutParams.leftMargin;
				right = layoutParams.rightMargin;
				top = layoutParams.topMargin;
				bottom = layoutParams.bottomMargin;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				break;
			case MotionEvent.ACTION_MOVE:
				x_new = (int)event.getRawX();
				y_new = (int)event.getRawY();
				x_cha = x_new-x_down;
				y_cha = y_new-y_down;
				
				if ((x_cha+left) > -300 &&
						(x_cha+left+layout_w) < width + 300 &&
						(y_cha+top+layout_h) < height + 300&& 
						(y_cha+top) > -300) {
						layoutParams.leftMargin = x_cha+left;
						layoutParams.topMargin = y_cha+top;
						layoutParams.rightMargin = right-x_cha;
						layoutParams.bottomMargin = bottom-y_cha;

						layout.setLayoutParams(layoutParams);
					}else {
						if((x_cha+left) > -300){
							layoutParams.leftMargin = x_cha+left;
						}
						if ((y_cha+top+layout_h) < height + 300){
							layoutParams.bottomMargin = bottom-y_cha;
						}
						if ((x_cha+left+layout_w) < width + 300){
							layoutParams.rightMargin = right-x_cha;
						}
						if ((y_cha+top) > -300){
							layoutParams.topMargin = y_cha+top;
							
						}
				}
				break;
			case MotionEvent.ACTION_UP:
				for (int i = 1; i < TouchImageViewActivity.content.getChildCount(); i++) {
					FrameLayout view = (FrameLayout)TouchImageViewActivity.content.getChildAt(i);
					if (view != layout) {
						view.getChildAt(0).setVisibility(View.INVISIBLE);
						view.getChildAt(2).setVisibility(View.INVISIBLE);
						view.getChildAt(3).setVisibility(View.INVISIBLE);
					}
				}
				layout.getChildAt(0).setVisibility(View.VISIBLE);
				layout.getChildAt(2).setVisibility(View.VISIBLE);
				layout.getChildAt(3).setVisibility(View.VISIBLE);
				break;
			case MotionEvent.ACTION_POINTER_UP:
				break;
			}
		return true;
	}
}
