package com.isayyo.app.editimg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.LogUtil.log;
import com.isayyo.app.R;
import com.isayyo.app.SelectImagesTopBar;
import com.isayyo.app.SelectImagesTopBar.OnTitleLeftButtonClickListener;
import com.isayyo.app.SelectImagesTopBar.OnTitleRightButtonClickListener;
import com.isayyo.app.utils.SmartBarUtils;
import com.isayyo.push.DemoApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class TouchImageViewActivity extends Activity {

	private Context mContext;
	private HuaBanView banView;
	private TuyaView tuyaView;
	public static List<TextView> list;
	private SharedPreferences sp;
	private int page = 1;
	 private long mLastClickTime = 0;
	 private long mLastClickTime1 = 0;
	public static FrameLayout content;
	private ImageView iv1;
	private ImageButton imageButton;
	private ImageButton imageButton2;
//	private ImageButton imageButton3;
//	private ImageButton imageButton4;
	//private ImageButton imageButton5;
	
	private Button back;
	private Button remove;
	
	private RadioGroup group1;
	private RadioGroup group2;
	
//	private ImageButton button;
//	private ImageButton button2;
	
	private LinearLayout layout;
	private LinearLayout layout2;
	private SelectImagesTopBar layout3;
	private SelectImagesTopBar layout4;
	private Bitmap bitmap;
	private Bitmap newbitmap;
	private DragImageView myIv;
	private int currentColor = 0;
	private int currentWidth = 16;
	
	int index;
	public static float widthScreen = 1080;
	public int heightScreen;

	private LayoutInflater inflater;
	private DemoApplication imagesApplication;
	private List<String> images;
	private String path;
	private ProgressDialog mProgressDialog;
	private Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				mProgressDialog.dismiss();
				if (images.size() == 1) {
					TouchImageViewActivity.this.setResult(120);
					log.e("touchimageviewactivity", "set result 120");
					finish();
				}
				else {
					finish(); 
				}
			}
		};
	};
	/** Called when the activity is first created. */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.edit_feeling);
		content = (FrameLayout)findViewById(R.id.content);
		list = new ArrayList<TextView>();
		SmartBarUtils.hide(getWindow().getDecorView());
		content.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		});
		
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		widthScreen = width;
		
		android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams)
				content.getLayoutParams();
		params.height = width;
		params.width = width;
		content.setLayoutParams(params);
		
		currentWidth = 16;
		group1 = (RadioGroup)findViewById(R.id.radioGroup1);
		group1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				int radioButtonId = group.getCheckedRadioButtonId();
				//根据ID获取RadioButton的实例
				switch (radioButtonId) {
				case R.id.width2:
					tuyaView.setPaintWidth(16);
					currentWidth = 16;
					break;
				case R.id.width3:
					tuyaView.setPaintWidth(24);
					currentWidth = 24;
					break;
				case R.id.width4:
					tuyaView.setPaintWidth(32);
					currentWidth = 32;
					break;
				case R.id.width5:
					tuyaView.setPaintWidth(40);
					currentWidth = 40;
					break;
				case R.id.width6:
					tuyaView.setPaintWidth(48);
					currentWidth = 48;
					break;
				default:
					break;
				}
				
			}
		});
		
		currentColor = getResources().getColor(R.color.color1);
		group2 = (RadioGroup)findViewById(R.id.radioGroup2);
		group2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				int radioButtonId = group.getCheckedRadioButtonId();
				//根据ID获取RadioButton的实例
				switch (radioButtonId) {
				case R.id.color1:
					tuyaView.setColor(getResources().getColor(R.color.color1));
					currentColor = getResources().getColor(R.color.color1);
					break;
				case R.id.color2:
					tuyaView.setColor(getResources().getColor(R.color.color2));
					currentColor = getResources().getColor(R.color.color2);
					break;
				case R.id.color3:
					tuyaView.setColor(getResources().getColor(R.color.color3));
					currentColor = getResources().getColor(R.color.color3);
					break;
				case R.id.color4:
					tuyaView.setColor(getResources().getColor(R.color.color4));
					currentColor = getResources().getColor(R.color.color4);
					break;
				case R.id.color5:
					tuyaView.setColor(getResources().getColor(R.color.color5));
					currentColor = getResources().getColor(R.color.color5);
					break;
				case R.id.color6:
					tuyaView.setColor(getResources().getColor(R.color.color6));
					currentColor = getResources().getColor(R.color.color6);
					break;
				case R.id.color7:
					tuyaView.setColor(getResources().getColor(R.color.color7));
					currentColor = getResources().getColor(R.color.color7);
					break;
				case R.id.color8:
					tuyaView.setColor(getResources().getColor(R.color.color8));
					currentColor = getResources().getColor(R.color.color8);
					break;
				default:
					break;
				}
				
			}
		});
		
		index = getIntent().getExtras().getInt("index", 0);
		imagesApplication = (DemoApplication)getApplication();
		images = imagesApplication.getImagesPath();
		path = images.get(index);
		bitmap = BitmapFactory.decodeFile(path); 
		Drawable drawable = new BitmapDrawable(getResources(), bitmap);
		content.setBackground(drawable);
			
		imageButton = (ImageButton)findViewById(R.id.tucao);
		imageButton2 = (ImageButton)findViewById(R.id.biaoqing);
		//imageButton5 = (ImageButton)findViewById(R.id.huaban);
		
		layout = (LinearLayout)findViewById(R.id.bottom1);
		layout2 = (LinearLayout)findViewById(R.id.bottom2);
		layout3 = (SelectImagesTopBar)findViewById(R.id.top1);
		layout4 = (SelectImagesTopBar)findViewById(R.id.top2);
		initTitleBar();
		back = (Button)findViewById(R.id.back);
		remove = (Button)findViewById(R.id.remove);
		
		imageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
				        return;
				          }
				    mLastClickTime = SystemClock.elapsedRealtime();

				Intent intent = new Intent();
				intent.setClass(TouchImageViewActivity.this, ChooseDialogActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		
		imageButton2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (SystemClock.elapsedRealtime() - mLastClickTime1 < 500) {
			        return;
			          }
			    mLastClickTime1 = SystemClock.elapsedRealtime();

				Intent intent = new Intent();
				intent.setClass(TouchImageViewActivity.this, ChooseFaceActivity.class);
				startActivityForResult(intent, 1);
			}
		});
		
		

		

		

		
		/*button2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams)
						layout.getLayoutParams();
				android.widget.LinearLayout.LayoutParams params2 = (android.widget.LinearLayout.LayoutParams)
						layout2.getLayoutParams();
				layout2.setLayoutParams(params);
				layout.setLayoutParams(params2);
				
				android.widget.LinearLayout.LayoutParams params3 = (android.widget.LinearLayout.LayoutParams)
						layout3.getLayoutParams();
				android.widget.LinearLayout.LayoutParams params4 = (android.widget.LinearLayout.LayoutParams)
						layout4.getLayoutParams();
				layout3.setLayoutParams(params4);
				layout4.setLayoutParams(params3);
				
				/*banView.setDrawingCacheEnabled(true);
				banView.buildDrawingCache();
				Bitmap bitmap = banView.getDrawingCache();
				ImageView imageView = new ImageView(TouchImageViewActivity.this);
				
				imageView.setImageBitmap(bitmap);
				imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				
				//banView.setAbility(false);
				//banView.setTag("yes");
				tuyaView.setAbility(false);
				tuyaView.setTag("yes");
			}
		});*/
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//banView.undo();
				tuyaView.undo();
			}
		});
		
		remove.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				content.removeView(tuyaView);
				tuyaView = new TuyaView(TouchImageViewActivity.this, content.getWidth(), content.getHeight());
				tuyaView.setColor(currentColor);
				tuyaView.setPaintWidth(currentWidth);
				content.addView(tuyaView);
			}
		});
		
		sp = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
		retrievePost(2);
		retrievePost(3);
		retrievePost(4);
	}

	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (page == 1) {
			if (content.getChildCount() > 1) {
				android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (images.size() == 1) {
							TouchImageViewActivity.this.setResult(122);
							finish();
						}
						else {
							finish();
						}
					}
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(TouchImageViewActivity.this)
					.setMessage("是否放弃此次编辑？")
					.setPositiveButton("确定", listener)
					.setNegativeButton("取消", null);
				builder.create()
				.show();
			} else {
				if (images.size() == 1) {
					android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							TouchImageViewActivity.this.setResult(122);
							finish();
						}
					};
					AlertDialog.Builder builder = new AlertDialog.Builder(TouchImageViewActivity.this)
						.setMessage("是否放弃本次编辑？")
						.setPositiveButton("放弃", listener)
						.setNegativeButton("取消", null);
					builder.create()
						.show();
				}
				else {
					finish();
				}
			}
		} else {
			if (tuyaView.isDirty) {
				android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						page = 1;
						android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams)
							layout.getLayoutParams();
						android.widget.LinearLayout.LayoutParams params2 = (android.widget.LinearLayout.LayoutParams)
							layout2.getLayoutParams();
						layout2.setLayoutParams(params);
						layout.setLayoutParams(params2);
					
						android.widget.LinearLayout.LayoutParams params3 = (android.widget.LinearLayout.LayoutParams)
							layout3.getLayoutParams();
						android.widget.LinearLayout.LayoutParams params4 = (android.widget.LinearLayout.LayoutParams)
							layout4.getLayoutParams();
						layout3.setLayoutParams(params4);
						layout4.setLayoutParams(params3);
					
						//content.removeView(banView);
						content.removeView(tuyaView);
					}
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(TouchImageViewActivity.this)
					.setMessage("是否放弃编辑画板？")
					.setPositiveButton("确定", listener)
					.setNegativeButton("取消", null);
				builder.create()
					.show();
			} else {
				page = 1;
				android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams)
						layout.getLayoutParams();
					android.widget.LinearLayout.LayoutParams params2 = (android.widget.LinearLayout.LayoutParams)
						layout2.getLayoutParams();
					layout2.setLayoutParams(params);
					layout.setLayoutParams(params2);
				
					android.widget.LinearLayout.LayoutParams params3 = (android.widget.LinearLayout.LayoutParams)
						layout3.getLayoutParams();
					android.widget.LinearLayout.LayoutParams params4 = (android.widget.LinearLayout.LayoutParams)
						layout4.getLayoutParams();
					layout3.setLayoutParams(params4);
					layout4.setLayoutParams(params3);
				
					//content.removeView(banView);
					content.removeView(tuyaView);
			}
		}
	}



	private void retrievePost(final int type) {
		AsyncHttpClient client = new AsyncHttpClient();
		String url = "http://120.24.56.40/server/retrieveIconPath.php";
		RequestParams params1 = new RequestParams();
		params1.put("icontype", type);
		JsonHttpResponseHandler postHandler = new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers, JSONArray postJsonArray) {
                    
                    int length = postJsonArray.length();
                    String str = "imageRes"+String.valueOf(type);
                    String str1 = "imagePath"+String.valueOf(type);
                    System.out.println(postJsonArray.toString());
                    
                    Set<String> set = new HashSet<String>();
                    for (int i = 0; i < length; i++) {
                    	try {
							set.add(postJsonArray.getJSONObject(i).getString("iconpath"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }
                
                    Editor editor = sp.edit();
                    editor.putInt(str, length);
                    editor.putStringSet(str1, set);
                    editor.commit();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, responseString, throwable);
			}
			
			
		};
		
		client.post(url, params1, postHandler);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (bitmap != null) bitmap.recycle();
		if (newbitmap != null) newbitmap.recycle();
		bitmap = null;
		newbitmap = null;
		System.gc();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (resultCode) {
		case RESULT_OK:
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			int width1 = wm.getDefaultDisplay().getWidth();
			
			boolean local = data.getBooleanExtra("local", true);
			final int result = data.getIntExtra("result", 0);
			String result2 = null;
			if (local == false) result2 = data.getStringExtra("result2");
			
			FrameLayout layout = (FrameLayout)findViewById(R.id.content);
			
			final FrameLayout frameLayout = new FrameLayout(this);
			LayoutParams layoutParams3 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			//layoutParams3.gravity = Gravity.CENTER;
			frameLayout.setLayoutParams(layoutParams3);
			
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			imageView.setImageResource(R.drawable.square);
			imageView.setScaleX((float)0.85);
			imageView.setScaleY((float)0.85);
			
			DeleteImageView deleteImageView = new DeleteImageView(this);
			LayoutParams layoutParams7 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			deleteImageView.setLayoutParams(layoutParams7);
			deleteImageView.setImageResource(R.drawable.chacha);
			
		
			final DragImageView dragImageView = new DragImageView(this);
			LayoutParams layoutParams6 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			layoutParams6.gravity = Gravity.CENTER;
			layoutParams6.width = (int)(width1*400/1080.0);
			layoutParams6.height = (int)(width1*400/1080.0);
			dragImageView.setLayoutParams(layoutParams6);
			if (local) {
				dragImageView.setImageResource(result);
			} else {
				ImageLoader.getInstance().loadImage(result2, new ImageLoadingListener() {
				
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
						dragImageView.setImageBitmap(arg2);
					}
				
					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub
					}
				});
			}
			//dragImageView.setImageResource(result);
			
			final ZoomImageView zoomImageView = new ZoomImageView(this);
			final LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			zoomImageView.setLayoutParams(layoutParams);
			zoomImageView.setImageResource(R.drawable.gan);
			zoomImageView.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					layoutParams.topMargin = (int)(0.8245*frameLayout.getHeight());
					layoutParams.leftMargin = (int)(0.8245*frameLayout.getWidth());
					zoomImageView.setLayoutParams(layoutParams);
				}
			});
			
			frameLayout.addView(imageView);
			frameLayout.addView(dragImageView);
			frameLayout.addView(deleteImageView);
			frameLayout.addView(zoomImageView);
			
			frameLayout.setTag("no");
			layout.addView(frameLayout);
			
			for (int i = 1; i < layout.getChildCount(); i++) {
				FrameLayout view = (FrameLayout)layout.getChildAt(i);
				if (view != frameLayout) {
					view.getChildAt(0).setVisibility(View.INVISIBLE);
					view.getChildAt(2).setVisibility(View.INVISIBLE);
					view.getChildAt(3).setVisibility(View.INVISIBLE);
				}
			}
			//dragImageView.setScaleX((float)imageView.getWidth()/dragImageView.getWidth());
			//dragImageView.setScaleY((float)imageView.getHeight()/dragImageView.getHeight());
			
			switch (requestCode) {
				case 0:
				final MyEditText editText = new MyEditText(this);
				final LayoutParams layoutParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams2.gravity = Gravity.CENTER;
				editText.setBackground(null);
				editText.setHeight((int)(width1*188/1080.0));
				editText.setWidth((int)(width1*262/1080.0));
				editText.setGravity(Gravity.CENTER);
				editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
				editText.setLayoutParams(layoutParams2);
				editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(28)}); 
				editText.setHint("吐槽28字以内");
				editText.setHintTextColor(Color.parseColor("#B2B2B2"));
				editText.setTextSize(15);
				list.add(editText);
				
				frameLayout.addView(editText);
				
				frameLayout.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						double top = 0;
						double left = 0;
						if (result == R.drawable.a1 || result == R.drawable.a2) {
							top = 0.05;
						} else if (result == R.drawable.a3 || result == R.drawable.a4) {
							top = -0.1;
						} else if (result == R.drawable.a5) {
							top = -0.1;
							left = -0.05;
						} else if (result == R.drawable.a6) {
							top = -0.1;
						} else if (result == R.drawable.a7) {
							left = -0.1;
						} else if (result == R.drawable.a8) {
							top = 0.1;
						} else if (result == R.drawable.a9) {
						} else if (result == R.drawable.a10) {
							top = -0.05;
							left = 0.05;
						} else if (result == R.drawable.a11) {
							left = -0.05;
						} else if (result == R.drawable.a12) {
							left = -0.05;
							top = -0.05;
						}
						layoutParams2.topMargin = (int)(top*frameLayout.getHeight());
						layoutParams2.leftMargin = (int)(left*frameLayout.getWidth());
						editText.setLayoutParams(layoutParams2);
					}
				});
			}
			
			break;

		default:
			break;
		}
	}
	public Bitmap resizeBitmap(Bitmap bitmap){
		final int maxSize = 600;
		int outWidth;
		int outHeight;
		int inWidth = bitmap.getWidth();
		int inHeight = bitmap.getHeight();
		if(inWidth > inHeight){
		    outWidth = maxSize;
		    outHeight = (inHeight * maxSize) / inWidth; 
		} else {
		    outHeight = maxSize;
		    outWidth = (inWidth * maxSize) / inHeight; 
		}

		Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
		return resizedBitmap;
		
	}
	public void saveBitmap(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<String> edited = imagesApplication.getEditedImagesPath();
				String newfile = edited.get(index);
				if (newfile == "") newfile = Environment.getExternalStorageDirectory() + "/isayyo/temp" + Integer.toString(index) +Integer.toString(index) + ".jpg";
				File file = new File(newfile);
		        if(file.exists()){   
		            file.delete();
		        }   
		        FileOutputStream out;   
		        try {   
		            out = new FileOutputStream(file);
		            Bitmap resizeBmp = resizeBitmap(newbitmap);
		            if (resizeBmp.compress(Bitmap.CompressFormat.JPEG, 100, out)) {   
		                out.flush();   
		                out.close();
		                edited.set(index, newfile);
		                imagesApplication.setEditedImagesPath(edited);
		            }
		        } catch (FileNotFoundException e) {   
		            e.printStackTrace();   
		        } catch (IOException e) {   
		            e.printStackTrace();   
		        }
		        mhandler.sendEmptyMessage(1);
			}
		}).start();  
	}
	private void initTitleBar() {
		layout3.setLeftBtnClickable(true);
		layout3.setTitleText("吐槽");
		layout3.setRightButtonVISIBLE(true);
		layout3.setCenterTextVISIBLE(false);
		if (images.size() == 1) layout3.setLeftButtonIcon(this, R.drawable.cancel);
		else layout3.setLeftButtonIcon(this, R.drawable.backw_selector);
		layout3.setRightButtonIcon(this, R.drawable.next_selector);
		layout3.setLeftButtonBackgroundNULL(this);
		layout3.setTitleTextColor(0xffffffff);
		//吐槽topbar的返回函数
		layout3.setLeftButtonClickListener(new OnTitleLeftButtonClickListener() {
			@Override
			public void onLeftButtonClick(View v) {
				// TODO Auto-generated method stub
				if (content.getChildCount() > 1) {
					android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (images.size() == 1) {
								TouchImageViewActivity.this.setResult(122);
								finish();
							}
							else {
								finish();
							}
						}
					};
					AlertDialog.Builder builder = new AlertDialog.Builder(TouchImageViewActivity.this)
						.setMessage("是否放弃此次编辑？")
						.setPositiveButton("确定", listener)
						.setNegativeButton("取消", null);
					builder.create()
						.show();
				} else {
					if (images.size() == 1) {
						android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								TouchImageViewActivity.this.setResult(122);
								finish();
							}
						};
						AlertDialog.Builder builder = new AlertDialog.Builder(TouchImageViewActivity.this)
							.setMessage("是否放弃本次编辑？")
							.setPositiveButton("放弃", listener)
							.setNegativeButton("取消", null);
						builder.create()
							.show();
					}
					else {
						finish();
					}
				}
			}
		});
		//吐槽topbar的next函数
		layout3.setRightButtonClickListener(new OnTitleRightButtonClickListener() {
			
			@Override
			public void onRightButtonClick(View v1) {
				final View v = v1;
				// TODO Auto-generated method stub
				boolean flag = true;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getText().toString().equals("")) {
						flag = false;
						break;
					}
				}
				
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				if (flag) {
					page = 2;
					for (int i = 1; i < content.getChildCount(); i++) {
						FrameLayout view = (FrameLayout)content.getChildAt(i);
							view.getChildAt(0).setVisibility(View.INVISIBLE);
							view.getChildAt(2).setVisibility(View.INVISIBLE);
							view.getChildAt(3).setVisibility(View.INVISIBLE);
					}
					
					android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams)
						layout.getLayoutParams();
					android.widget.LinearLayout.LayoutParams params2 = (android.widget.LinearLayout.LayoutParams)
						layout2.getLayoutParams();
					layout2.setLayoutParams(params);
					layout.setLayoutParams(params2);
					
					android.widget.LinearLayout.LayoutParams params3 = (android.widget.LinearLayout.LayoutParams)
						layout3.getLayoutParams();
					android.widget.LinearLayout.LayoutParams params4 = (android.widget.LinearLayout.LayoutParams)
						layout4.getLayoutParams();
					layout3.setLayoutParams(params4);
					layout4.setLayoutParams(params3);
				
					/*banView = new HuaBanView(TouchImageViewActivity.this);
					content.addView(banView);*/
					tuyaView = new TuyaView(TouchImageViewActivity.this, content.getWidth(), content.getHeight());
					tuyaView.setColor(currentColor);
					tuyaView.setPaintWidth(currentWidth);
					content.addView(tuyaView);
				} else {
					Toast.makeText(TouchImageViewActivity.this, "似乎没有吐槽啊", Toast.LENGTH_SHORT).show();;
				}
				
				// TODO Auto-generated method stub
				layout3.setFocusable(true);
				layout3.setFocusableInTouchMode(true);
				layout3.requestFocus();
				layout3.requestFocusFromTouch();
				
			}
		});
		layout4.setLeftBtnClickable(true);
		layout4.setTitleText("涂鸦");
		layout4.setRightButtonVISIBLE(true);
		layout4.setCenterTextVISIBLE(false);
		layout4.setLeftButtonIcon(this, R.drawable.backw_selector);
		layout4.setRightButtonIcon(this, R.drawable.ok_selector);
		layout4.setLeftButtonBackgroundNULL(this);
		layout4.setTitleTextColor(0xffffffff);
		//画板topbar的返回函数
		layout4.setLeftButtonClickListener(new OnTitleLeftButtonClickListener() {
			
			@Override
			public void onLeftButtonClick(View v) {
				// TODO Auto-generated method stub
				if (tuyaView.isDirty) {
					android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							page = 1;
							android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams)
								layout.getLayoutParams();
							android.widget.LinearLayout.LayoutParams params2 = (android.widget.LinearLayout.LayoutParams)
								layout2.getLayoutParams();
							layout2.setLayoutParams(params);
							layout.setLayoutParams(params2);
						
							android.widget.LinearLayout.LayoutParams params3 = (android.widget.LinearLayout.LayoutParams)
								layout3.getLayoutParams();
							android.widget.LinearLayout.LayoutParams params4 = (android.widget.LinearLayout.LayoutParams)
								layout4.getLayoutParams();
							layout3.setLayoutParams(params4);
							layout4.setLayoutParams(params3);
						
							//content.removeView(banView);
							content.removeView(tuyaView);
						}
					};
					AlertDialog.Builder builder = new AlertDialog.Builder(TouchImageViewActivity.this)
						.setMessage("是否放弃编辑画板？")
						.setPositiveButton("确定", listener)
						.setNegativeButton("取消", null);
					builder.create()
						.show();
				} else {
					page = 1;
					android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams)
							layout.getLayoutParams();
						android.widget.LinearLayout.LayoutParams params2 = (android.widget.LinearLayout.LayoutParams)
							layout2.getLayoutParams();
						layout2.setLayoutParams(params);
						layout.setLayoutParams(params2);
					
						android.widget.LinearLayout.LayoutParams params3 = (android.widget.LinearLayout.LayoutParams)
							layout3.getLayoutParams();
						android.widget.LinearLayout.LayoutParams params4 = (android.widget.LinearLayout.LayoutParams)
							layout4.getLayoutParams();
						layout3.setLayoutParams(params4);
						layout4.setLayoutParams(params3);
					
						//content.removeView(banView);
						content.removeView(tuyaView);
				}
			}
		});
		//画板topbar的next函数
		layout4.setRightButtonClickListener(new OnTitleRightButtonClickListener() {
			
			@Override
			public void onRightButtonClick(View v) {
				// TODO Auto-generated method stub
				tuyaView.setAbility(false);
				tuyaView.setTag("yes");
			
				v.setFocusable(true);
				v.setFocusableInTouchMode(true);
				v.requestFocus();
				v.requestFocusFromTouch();
				FrameLayout frameLayout = content;
			
				for (int i = 1; i < frameLayout.getChildCount(); i++) {
					if (frameLayout.getChildAt(i).getTag().toString() != "yes") {
						FrameLayout view = (FrameLayout) frameLayout.getChildAt(i);
						view.getChildAt(0).setEnabled(false);
						view.getChildAt(0).setVisibility(View.GONE);
				
						view.getChildAt(2).setEnabled(false);
						view.getChildAt(2).setVisibility(View.GONE);
				
						view.getChildAt(3).setEnabled(false);
						view.getChildAt(3).setVisibility(View.GONE);
					}
				}
			
				// TODO Auto-generated method stub
				View view = getWindow().getDecorView();  
				view.setDrawingCacheEnabled(true);  
				view.buildDrawingCache();  
				newbitmap = view.getDrawingCache();
				Rect frame = new Rect();  
				view.getWindowVisibleDisplayFrame(frame);  
				int toHeight = frame.top;
				Bitmap newbitmap1;
				newbitmap1 = Bitmap.createBitmap(newbitmap, (int)content.getX(), (int)content.getY()+toHeight+(content.getWidth()-content.getHeight())/2, content.getWidth(), content.getWidth());

				newbitmap = Bitmap.createScaledBitmap(newbitmap1, 600, 600, true);
				newbitmap1.recycle();
				newbitmap1 = null;

				view.setDrawingCacheEnabled(false);
				mProgressDialog = ProgressDialog.show(TouchImageViewActivity.this, null, "正在保存更改后的图片");
				saveBitmap();
			}
		});
	}
}