package com.isayyo.app.editimg;

import android.content.Context;
import android.graphics.Paint;
import android.text.Layout;
import android.text.Selection;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class MyEditText extends EditText {
	float textsize = 44;
	float widthScreen = 1080;
	float minTextSize = 28;
	float toTextSize = 28;

	public MyEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initialise();
		width = TouchImageViewActivity.content.getWidth();
		height = TouchImageViewActivity.content.getHeight();
		textsize = getTextSize();
		widthScreen = TouchImageViewActivity.widthScreen;
		minTextSize = 22*widthScreen/1080;
	}

	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initialise();
		width = TouchImageViewActivity.content.getWidth();
		height = TouchImageViewActivity.content.getHeight();
		textsize = getTextSize();
		widthScreen = TouchImageViewActivity.widthScreen;
		minTextSize = 22*widthScreen/1080;
	}

	public MyEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initialise();
		width = TouchImageViewActivity.content.getWidth();
		height = TouchImageViewActivity.content.getHeight();
		textsize = getTextSize();
		widthScreen = TouchImageViewActivity.widthScreen;
		minTextSize = 22*widthScreen/1080;
	}
	private void initialise() {
        mTestPaint = new Paint();
        mTestPaint.set(this.getPaint());
        //max size defaults to the initially specified text size unless it is too small
    }

    /* Re size the font so the specified text fits in the text box
     * assuming the text box is the specified width.
     */
    private void refitText(String text, int textHeight) 
    { 	
        if (textHeight <= 0)
            return;
       System.out.println(getTextSize());
        mTestPaint.set(this.getPaint());
        if (text.length() > 22) {
        	toTextSize = textsize-((text.length()-1)-(text.length()-22)/2)*widthScreen/720;
        	if (toTextSize >= minTextSize) setTextSize(TypedValue.COMPLEX_UNIT_PX, toTextSize);
        } else if (text.length() > 7) {
        	toTextSize = textsize-(text.length()-1)*widthScreen/720;
        	if (toTextSize >= minTextSize) setTextSize(TypedValue.COMPLEX_UNIT_PX, toTextSize);
        }
        
        //this.setTextSize(TypedValue.COMPLEX_UNIT_PX, lo);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
		refitText(text.toString(), getHeight());
        
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
    }

    //Attributes
    private Paint mTestPaint;
    
	
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
	int off = 0;
	
	FrameLayout layout;
	LayoutParams layoutParams;

	public boolean onTouchEvent(MotionEvent event) {
		Layout layout2 = getLayout();  
        int line = 0;  
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				line = layout2.getLineForVertical(getScrollY()+ (int)event.getY());
	            off = layout2.getOffsetForHorizontal(line, (int)event.getX());  
	            Selection.setSelection(getEditableText(), off);
	           
				
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
				x_new = (int)event.getRawX();
				y_new = (int)event.getRawY();
				x_cha = x_new-x_down;
				y_cha = y_new-y_down;
				if ((x_cha < 20) && (x_cha > -20) && (y_cha < 20) && (y_cha > -20)) {
					setFocusable(true);
					setFocusableInTouchMode(true);
					requestFocus();
					requestFocusFromTouch();
				
					InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(this, 0);
				}
				
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
