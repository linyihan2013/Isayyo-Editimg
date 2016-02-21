package com.isayyo.app.editimg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.isayyo.app.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.LinearLayout.LayoutParams;

public class LocalFragment extends Fragment {
	private GridLayout glayout;
    private SharedPreferences sp;
    private boolean dirty = false;
    
    // 图片封装为一个数组
    private int[] icon = {R.drawable.b1, R.drawable.b2, R.drawable.b3, R.drawable.b4,
    		R.drawable.b5, R.drawable.b6, R.drawable.b7, R.drawable.b8, 
    		R.drawable.b9, R.drawable.b10, R.drawable.b11, R.drawable.b12};
    
    private int width = 1080;
    private int count = 12;
    private String str;
    private View view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		sp = getActivity().getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.choose_feeling, container,
				false);
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
		glayout = (GridLayout)view.findViewById(R.id.gridLayout);
		
        return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		init();
	}
	
	public void loadImage(int index) {
		if (dirty == false) {
			dirty = true;
			str = "imageRes"+String.valueOf(index+1);
		 String str1 = "imagePath"+String.valueOf(index+1);
		 
		 count = sp.getInt(str, 12);
		 String url = "";
		 Set<String> set = sp.getStringSet(str1, null);
		 System.out.println("set"+set);
		 Iterator<String> iterable = set.iterator();
		 
		 if (index == 0){
			 url = "http://img.isayyo.com/mark/";
		 }
		 else if (index == 1){
			 url = "http://img.isayyo.com/mark/";
		 }
		 else if (index == 2){
			 url = "http://img.isayyo.com/fantuan/";
		 }
		 else if (index == 3){
			 url = "http://img.isayyo.com/faceemoji/";
		 }

		 for (int i = 0; i < count; i++) {
			 final String iconpath = url+iterable.next();
			 
			 final ImageView child = new ImageView(getActivity());
			 child.setClickable(false);
			 //child.setImageResource(icon[0]);
			// child.setVisibility(View.INVISIBLE);
			 
			 DisplayImageOptions options = new DisplayImageOptions.Builder()
			 	.cacheOnDisc(true)
			 	.build();
			 
			 ImageLoader.getInstance().displayImage(iconpath, child, options, new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					// TODO Auto-generated method stub
					child.setImageBitmap(arg2);
					child.setClickable(true);
					//child.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					// TODO Auto-generated method stub
					
				}
			});
	         
	         LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	         layoutParams.width = 290*width/1080;
	         layoutParams.height = 290*width/1080;
	         child.setLayoutParams(layoutParams);
	         //child.setPadding(20*width/1080, 20*width/1080, 20*width/1080, 20*width/1080);
	        
	         glayout.addView(child);
	         child.setId(i);
	         child.setOnClickListener(new OnClickListener() {
	        	 @Override
	        	 public void onClick(View v) {
	        		 // TODO Auto-generated method stub
	        		 Intent intent = new Intent();
	        		 intent.putExtra("local", false);
	        		 intent.putExtra("result2", iconpath);
	        		 getActivity().setResult(Activity.RESULT_OK, intent);
	        		 getActivity().finish();
	        	 }
	         });
		 }
	}
	}
	
	public void init() {
		 for (int i = 0; i < icon.length; i++) {
			 final ImageView child = new ImageView(getActivity());
			 child.setImageResource(icon[i]);
	         
	         LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	         layoutParams.width = 290*width/1080;
	         layoutParams.height = 290*width/1080;
	         child.setLayoutParams(layoutParams);
	         
	         glayout.addView(child);
	         child.setId(i);
	         child.setOnClickListener(new OnClickListener() {
	        	 @Override
	        	 public void onClick(View v) {
	        		 // TODO Auto-generated method stub
	        		 Intent intent = new Intent();
	        		 intent.putExtra("result", icon[v.getId()]);
	        		 intent.putExtra("local", true);
	        		 getActivity().setResult(Activity.RESULT_OK, intent);
	        		 getActivity().finish();
	        	 }
	         });
		 }
	}
}
