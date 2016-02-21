package com.isayyo.app.editimg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isayyo.app.R;
import com.isayyo.app.utils.SmartBarUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class ChooseDialogActivity extends Activity {

	private GridLayout glayout;
	private ImageButton left;
  
    // 图片封装为一个数组
    private int[] icon = {R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4,
    		R.drawable.a5, R.drawable.a6, R.drawable.a7, R.drawable.a8, 
    		R.drawable.a9, R.drawable.a10, R.drawable.a11, R.drawable.a12};
    private int width = 1080;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_dialog);
		SmartBarUtils.hide(getWindow().getDecorView());
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
		
		left = (ImageButton)findViewById(R.id.dialog_left);
		left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				glayout = (GridLayout)findViewById(R.id.gridLayout);
				
				for (int i = 0; i < 12; i++) {
		        	ImageView child = new ImageView(ChooseDialogActivity.this);
		        	child.setImageResource(icon[i]);
		        	LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		        	layoutParams.width = 325*width/1080;
		        	layoutParams.height = 325*width/1080;
		        	child.setLayoutParams(layoutParams);
		        	child.setPadding(20*width/1080, 20*width/1080, 20*width/1080, 20*width/1080);
		        	
		        	glayout.addView(child);
		        	child.setId(i);
		        	child.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.putExtra("result", icon[v.getId()]);
							setResult(Activity.RESULT_OK, intent);
							finish();
						}
					});
				}
			}
		}.run();
	}
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finish();
		return true;
	}
	
}
