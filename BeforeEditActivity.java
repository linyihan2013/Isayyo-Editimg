package com.isayyo.app.editimg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.isayyo.app.R;
import com.isayyo.app.SelectImagesTopBar;
import com.isayyo.app.SelectImagesTopBar.OnTitleLeftButtonClickListener;
import com.isayyo.app.SelectImagesTopBar.OnTitleRightButtonClickListener;
import com.isayyo.app.listimages.ShowImgActivity;
import com.isayyo.app.utils.SmartBarUtils;
import com.isayyo.push.DemoApplication;

public class BeforeEditActivity extends Activity {
	private DemoApplication imagesApplication;
	private List<String> images;
	private Bitmap[] bitmaps;
	private Bitmap temp;
	private boolean has_edited[] = new boolean[4];//标记图片有没有修改过，初始化为false
	private SelectImagesTopBar topbar;
	private ImageView[] imageViews, fugaiViews;//图片层和覆盖层
	private FrameLayout[] frameLayout;
	//size是当前显示的图片张数
	private int size, width;
	private boolean ensure_next;//标记是否正常进入下一页
	private LinearLayout contentContainer, row1, row2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SmartBarUtils.hide(getWindow().getDecorView());
		setContentView(R.layout.activity_before_edit);
		initview();
		initTitleBar();
	}
	private void initview() {
		ensure_next = false;
		imageViews = new ImageView[4];
		fugaiViews = new ImageView[4];
		frameLayout = new FrameLayout[4];
		imagesApplication = (DemoApplication)getApplication();
		images = imagesApplication.getImagesPath();
		DisplayMetrics  dm = new DisplayMetrics();     
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		bitmaps = new Bitmap[4];
		topbar = (SelectImagesTopBar)findViewById(R.id.beforeEdit_top_bar);
		contentContainer = (LinearLayout)findViewById(R.id.beforeEdit_content);
		size = images.size();
		loadImages();
	}
	private void loadImages() {
		System.gc();
		for (int i = 0; i < size; i++) {
			temp = getCompressedBitmap(images.get(i));
			if (temp != null) {
				bitmaps[i] = temp;
			}
		}
		if (size == 1) {
			imageViews[0] = new ImageView(BeforeEditActivity.this);
			setitem(width, 0, contentContainer, imageViews[0]);
		} else if (size == 2) {
			imageViews[0] = new ImageView(BeforeEditActivity.this);
			imageViews[1] = new ImageView(BeforeEditActivity.this);
			row1 = new LinearLayout(this);
			row1.setLayoutParams(new LinearLayout.LayoutParams(width, width/2));
			row1.setOrientation(LinearLayout.HORIZONTAL);
			setitem(width / 2, 0, row1, imageViews[0]);
			setitem(width / 2, 1, row1, imageViews[1]);
			contentContainer.addView(row1);
		} else if (size == 3) {
			imageViews[0] = new ImageView(BeforeEditActivity.this);
			imageViews[1] = new ImageView(BeforeEditActivity.this);
			imageViews[2] = new ImageView(BeforeEditActivity.this);
			row1 = new LinearLayout(this);
			row1.setLayoutParams(new LinearLayout.LayoutParams(width, width/2));
			row1.setOrientation(LinearLayout.HORIZONTAL);
			setitem(width / 2, 0, row1, imageViews[0]);
			setitem(width / 2, 1, row1, imageViews[1]);
			contentContainer.addView(row1);
			row2 = new LinearLayout(this);
			row2.setLayoutParams(new LinearLayout.LayoutParams(width, width/2));
			row2.setOrientation(LinearLayout.HORIZONTAL);
			setitem(width / 2, 2, row2, imageViews[2]);
			contentContainer.addView(row2);
		} else if (size == 4) {
			imageViews[0] = new ImageView(BeforeEditActivity.this);
			imageViews[1] = new ImageView(BeforeEditActivity.this);
			imageViews[2] = new ImageView(BeforeEditActivity.this);
			imageViews[3] = new ImageView(BeforeEditActivity.this);
			row1 = new LinearLayout(this);
			row1.setLayoutParams(new LinearLayout.LayoutParams(width, width/2));
			row1.setOrientation(LinearLayout.HORIZONTAL);
			setitem(width / 2, 0, row1, imageViews[0]);
			setitem(width / 2, 1, row1, imageViews[1]);
			contentContainer.addView(row1);
			row2 = new LinearLayout(this);
			row2.setLayoutParams(new LinearLayout.LayoutParams(width, width/2));
			row2.setOrientation(LinearLayout.HORIZONTAL);
			setitem(width / 2, 2, row2, imageViews[2]);
			setitem(width / 2, 3, row2, imageViews[3]);
			contentContainer.addView(row2);
		}
	}
	private void setitem(int w, final int index, LinearLayout parent, ImageView ditu) {
		fugaiViews[index] = new ImageView(BeforeEditActivity.this);
		ditu.setImageBitmap(bitmaps[index]);
		fugaiViews[index].setImageResource(R.drawable.click);
		ditu.setScaleType(ScaleType.FIT_CENTER);
		fugaiViews[index].setScaleType(ScaleType.FIT_CENTER);
		frameLayout[index] = new FrameLayout(this);
		LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(w - 50, w - 50);
		flp.setMargins(25, 25, 25, 25);
		frameLayout[index].setLayoutParams(flp);
		frameLayout[index].setBackgroundColor(getResources().getColor(R.color.itembg));
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		ditu.setLayoutParams(lp);
		fugaiViews[index].setLayoutParams(lp);
		frameLayout[index].addView(ditu);
		frameLayout[index].addView(fugaiViews[index]);
		has_edited[index] = false;
		frameLayout[index].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (has_edited[index] == false) {
					Intent intent = new Intent();
					intent.putExtra("index", index);
					intent.setClass(BeforeEditActivity.this, TouchImageViewActivity.class);
					startActivityForResult(intent, index);
				}
				else {
					android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.putExtra("index", index);
							intent.setClass(BeforeEditActivity.this, TouchImageViewActivity.class);
							startActivityForResult(intent, index);
						}
					};
					AlertDialog.Builder builder = new AlertDialog.Builder(BeforeEditActivity.this)
						.setMessage("是否重新编辑图片？")
						.setPositiveButton("确定", listener)
						.setNegativeButton("取消", null);
					builder.create()
						.show();
				}
			}
		});
		parent.addView(frameLayout[index]);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//111需求码代表的是publish页面的回调
		if (requestCode == 111) {
			//112代表发表成功后的结束
			if (resultCode == 112) {
				if (data != null){
					//Bundle bundle = new Bundle();
					//bundle.putSerializable("NewPost", mPostItem);
					//Intent returnIntent = new Intent();
					//returnIntent.putExtras(bundle);
					System.out.println("beforeedit onactivityresult");
				}
				BeforeEditActivity.this.setResult(112,data);
				BeforeEditActivity.this.finish();
			}
		} else {
			renewImg(requestCode);
		}
		
	}
	private void renewImg(int index) {
		List<String> edited = imagesApplication.getEditedImagesPath();
		String editedimg = edited.get(index);
		if (editedimg != "") {
			imageViews[index].setImageBitmap(null);
			bitmaps[index].recycle();
			bitmaps[index] = null;
			System.gc();
			bitmaps[index] = getCompressedBitmap(editedimg);
			imageViews[index].setImageBitmap(bitmaps[index]);
			if (has_edited[index] == false) {
				has_edited[index] = true;
				frameLayout[index].removeViewAt(1);
			}
		}
	}
	private Bitmap getCompressedBitmap(String path) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options(); 
		newOpts.inJustDecodeBounds = true; 
        Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);   
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        int hh = 200;
        int ww = 200; 
        int be = 1;
        if (w >= h && w > ww) {  
            be = (int) (w / ww);  
        } else if (w < h && h > hh) {  
            be = (int) (h / hh);  
        }  
        if (be <= 0)  
            be = 1;  
        newOpts.inSampleSize = be;//设置采样率  
        newOpts.inJustDecodeBounds = false;
        newOpts.inDither = false;
        newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设  
        bitmap = BitmapFactory.decodeFile(path, newOpts);
        return bitmap; 
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				BeforeEditActivity.this.setResult(110);
				ensure_next = false;
				finish();
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(BeforeEditActivity.this)
			.setMessage("是否放弃本次编辑？")
			.setPositiveButton("放弃", listener)
			.setNegativeButton("取消", null);
		builder.create()
			.show();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		for (int i = 0; i < size; i++) {
			if (bitmaps[i] != null)
				bitmaps[i].recycle();
		}
		if (temp != null) {
			temp.recycle();
			temp = null;
		}
		System.gc();
		//如果没有正常进入下一个页面，删除存储的临时图片
		if (ensure_next == false) {
			for (int i = 0; i < images.size(); i++) {
				File file = new File(images.get(i));
				if (file != null) {
					if (file.exists()) file.delete();
				}
			}
			List<String> edited = imagesApplication.getEditedImagesPath();
			for (int i = 0; i < edited.size(); i++) {
				if (edited.get(i) != "") {
					File file = new File(edited.get(i));
					if (file != null) {
						if (file.exists()) file.delete();
					}
				}
			}
		}
		super.onDestroy();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.before_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void initTitleBar() {
		topbar.setLeftBtnClickable(true);
		topbar.setTitleText("吐槽");
		topbar.setRightButtonVISIBLE(true);
		topbar.setCenterTextVISIBLE(false);
		topbar.setLeftButtonIcon(this, R.drawable.cancel);
		topbar.setRightButtonIcon(this, R.drawable.next_selector);
		topbar.setLeftButtonBackgroundNULL(this);
		topbar.setTitleTextColor(0xffffffff);
		topbar.setLeftButtonClickListener(new OnTitleLeftButtonClickListener() {
			@Override
			public void onLeftButtonClick(View v) {
				// TODO Auto-generated method stub
				android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						BeforeEditActivity.this.setResult(110);
						ensure_next = false;
						finish();
					}
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(BeforeEditActivity.this)
					.setMessage("是否放弃本次编辑？")
					.setPositiveButton("放弃", listener)
					.setNegativeButton("取消", null);
				builder.create()
					.show();
			}
		});
		topbar.setRightButtonClickListener(new OnTitleRightButtonClickListener() {
			
			@Override
			public void onRightButtonClick(View v) {
				// TODO Auto-generated method stub
				ensure_next = true;
				startActivityForResult(new Intent(BeforeEditActivity.this, PublishActivity.class), 111);
			}
		});
	}
}
