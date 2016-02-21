package com.isayyo.app.editimg;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import cn.isayyo.app.bean.Post;

import com.isayyo.app.R;
import com.isayyo.app.utils.SmartBarUtils;

public class ChooseFaceActivity extends FragmentActivity {
	private Fragment[] fragments;
	private int index;
	// 当前fragment的index
	private int currentTabIndex = 0;
	private ImageButton[] mTabs;
	private SharedPreferences sp;
	
	private ImageButton left;
	private EmojiFragment emojiFragment1;
	private EmojiFragment emojiFragment2;
	private EmojiFragment emojiFragment3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_emoji);
		SmartBarUtils.hide(getWindow().getDecorView());
		left = (ImageButton)findViewById(R.id.emoji_left);
		left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		sp = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
		initView();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finish();
		return true;
	}
	
	private void initView() {
		mTabs = new ImageButton[3];
		mTabs[0] = (ImageButton) findViewById(R.id.emoji1);
		mTabs[1] = (ImageButton) findViewById(R.id.emoji2);
		mTabs[2] = (ImageButton) findViewById(R.id.emoji3);
		
		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);
		mTabs[0].setBackgroundColor(Color.parseColor("#686868"));
		mTabs[1].setBackgroundColor(Color.parseColor("#333333"));
		mTabs[2].setBackgroundColor(Color.parseColor("#333333"));
		
		emojiFragment1 = new EmojiFragment();
		emojiFragment2 = new EmojiFragment();
		emojiFragment3 = new EmojiFragment();
		
		emojiFragment1.setId(1);
		emojiFragment2.setId(2);
		emojiFragment3.setId(3);
		
		fragments = new Fragment[] { emojiFragment1, emojiFragment2, emojiFragment3};					
		
		getSupportFragmentManager().beginTransaction()
			.add(R.id.container, emojiFragment1)
			.add(R.id.container, emojiFragment2)
			.add(R.id.container, emojiFragment3)
			.hide(emojiFragment2)
			.hide(emojiFragment3)
			.show(emojiFragment1).commit();
	}
	
	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.emoji1:
			index = 0;
			break;
		case R.id.emoji2:
			index = 1;
			break;
		case R.id.emoji3:
			index = 2;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager()
					.beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.container, fragments[index]);
			}
			loadImage(index);
			trx.show(fragments[index]).commit();
		}
		
		 mTabs[currentTabIndex].setSelected(false);
		 mTabs[currentTabIndex].setBackgroundColor(Color.parseColor("#333333"));
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		mTabs[index].setBackgroundColor(Color.parseColor("#686868"));
		currentTabIndex = index;
		
	}
	
	public void loadImage(int index) {
		 if (index == 0){
			 emojiFragment1.loadImage(index);
		 }
		 else if (index == 1){
			 emojiFragment2.loadImage(index);
		 }
		 else if (index == 2){
			 emojiFragment3.loadImage(index);
		 }
	}
}
