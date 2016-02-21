package com.isayyo.app.editimg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
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
import cn.isayyp.app.widght.CustomProgressDialog;

import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.model.AccessControlList;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.model.TokenGenerator;
import com.alibaba.sdk.android.oss.model.OSSException.ExceptionType;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.alibaba.sdk.android.oss.storage.TaskHandler;
import com.alibaba.sdk.android.oss.util.OSSToolKit;
import com.isayyo.app.AppConstant;
import com.isayyo.app.R;
import com.isayyo.app.SelectImagesTopBar;
import com.isayyo.app.SelectImagesTopBar.OnTitleLeftButtonClickListener;
import com.isayyo.app.SelectImagesTopBar.OnTitleRightButtonClickListener;
import com.isayyo.app.utils.PostJsonArrayUtils;
import com.isayyo.app.utils.SmartBarUtils;
import com.isayyo.http.getPost;
import com.isayyo.push.DemoApplication;
import com.issayo.app.entity.PostImageItem;
import com.issayo.app.entity.PostItem;
import com.issayo.app.entity.TortureImageItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wallace.xlistview.view.MusicXListFragment;


public class TortureImageViewActivity extends Activity {

	private Context mContext;
	private HuaBanView banView;
	private TuyaView tuyaView;
	public static List<TextView> list;
	private OSSBucket bucket;
	public static FrameLayout content;
	private ImageView iv1;
	private ImageButton imageButton;
	private ImageButton imageButton2;
	 private long mLastClickTime = 0;
	 private long mLastClickTime1 = 0;
	private SharedPreferences sp;
	private final static int UPLOAD_SUCCESS = 0x01;
	private final static int UPLOAD_FAIL = 0x02;
	private static final String accessKey = "msbTOIO0FBOwFBNG"; // 测试代码没有考虑AK/SK的安全性
	private static final String screctKey = "JnhKdvE9xlZSuHKjkdloKDNOhnwqqm";
	private static CustomProgressDialog pdialog;
	private Button back;
	private Button remove;
	private int currentColor = 0;
	private int currentWidth = 16;
	private RadioGroup group1;
	private RadioGroup group2;
	public  static int have_select_emoji = 0;

	//标记是否已经发现上传出错
	private boolean have_showed_error, have_click_send_btn = false, have_draw_on_page = false; 
	//上传成功的个数和要上传文件的总数
	private int sucessNumber, sumOfFile;
	
	private LinearLayout layout;
	private LinearLayout layout2;
	private SelectImagesTopBar layout3;
	private SelectImagesTopBar layout4;
	private Bitmap bitmap;
	private Bitmap newbitmap;
	private DragImageView myIv;
	private int index;
	private int pindex;
	private int top;
	private int start;
	public static float widthScreen = 1080;
	public int heightScreen;
	private TortureImageItem timgItem;
	private PostImageItem pimgItem;
	private LayoutInflater inflater;
	private DemoApplication imagesApplication;
	private List<String> images;
	private String path;
	private ProgressDialog mProgressDialog;
	//tHandlers1和tHandlers2分别是原图和吐槽后图片的上传回调
	private TaskHandler[] tHandlers1 = new TaskHandler[4];
	private TaskHandler[] tHandlers2 = new TaskHandler[4];
	//上传回调函数
		private SaveCallback mSaveCallback = new SaveCallback() {
			@Override
			public void onProgress(String arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(String arg0, OSSException arg1) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = UPLOAD_FAIL;
				msg.obj = new String("上传图片失败");
				mHandler.sendMessage(msg);
			}
			
			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				sucessNumber++;
			}
		};
	private Handler ahandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				TortureImageUpload();

			}
		};
	};
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what == UPLOAD_SUCCESS) {
				pdialog.dismiss();
				have_click_send_btn = false;
				timer.cancel();
				Toast.makeText(TortureImageViewActivity.this, "上传成功",Toast.LENGTH_SHORT).show();
				//mProgressDialog.dismiss();
				Intent returnIntent = new Intent();
				returnIntent.putExtra("result","result");
				returnIntent.putExtra("pindex", pindex);
				returnIntent.putExtra("start", start);
				returnIntent.putExtra("top", top);
				Bundle bundle = new Bundle();
				bundle.putSerializable("TortureImageItem", timgItem);
				returnIntent.putExtras(bundle);
				setResult(1,returnIntent);
				finish();
			}
			else if (msg.what == UPLOAD_FAIL) {
				have_click_send_btn = false;
				if (have_showed_error == false) {
					pdialog.dismiss();
				//	mProgressDialog.dismiss();
					//停止倒计时器
					timer.cancel();
					//停止所有其他任务
					
						if (tHandlers1[0] != null) tHandlers1[0].cancel();
						
					
					//显示错误信息
					String info = (String)msg.obj;
					Toast.makeText(TortureImageViewActivity.this, info,Toast.LENGTH_SHORT).show();
					//删除数据库的记录
					new Thread(new Runnable() {
						@Override
						public void run() {
							ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
							nameValuePair.add(new BasicNameValuePair("tid", timgItem.getTortureId()));
							// 链接网页
							try {
								HttpClient httpClient = new DefaultHttpClient();
								HttpPost httpPost = new HttpPost(AppConstant.deleTorturePath);
								httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
								httpClient.execute(httpPost);
							} catch (Exception e) {
								
							}
						}
					}).start();
					layout4.setRightButtonVISIBLE(true);
					
					have_showed_error = true;
				}
			}
		};
	};
	static {
		OSSClient.setGlobalDefaultTokenGenerator(new TokenGenerator() {
		@Override
		public String generateToken(String httpMethod, String md5, String type, String date,
		String ossHeaders, String resource) {
			String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders + resource;
			return OSSToolKit.generateToken(accessKey, screctKey, content);
		}
		});
		OSSClient.setGlobalDefaultHostId("oss-cn-shenzhen.aliyuncs.com");
		OSSClient.setGlobalDefaultACL(AccessControlList.PRIVATE);
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.edit_feeling);
		content = (FrameLayout)findViewById(R.id.content);
		SmartBarUtils.hide(getWindow().getDecorView());
		list = new ArrayList<TextView>();
		
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
		Intent i = getIntent();
		Bundle bd = i.getExtras();
		pimgItem = (PostImageItem) bd.getSerializable("PostImageItem");
		index = getIntent().getExtras().getInt("index", 0);
		pindex = getIntent().getExtras().getInt("pindex", 0);
		top = getIntent().getExtras().getInt("top", 0);
		start = getIntent().getExtras().getInt("start", 0);
		imagesApplication = (DemoApplication)getApplication();
		images = imagesApplication.getImagesPath();
		path = images.get(index);
		String url = AppConstant.imageOrginialPath + path + ".jpg";
		Bitmap bitmap = imagesApplication.getBitmap();
    	final Drawable drawable = new BitmapDrawable(getResources(), bitmap);
    	content.setBackground(drawable);
    	/*
		ImageLoader.getInstance().loadImage(url, new SimpleImageLoadingListener(){

            @Override
            public void onLoadingComplete(String imageUri, View view,
                    Bitmap loadedImage) {
            super.onLoadingComplete(imageUri, view, loadedImage);

                //write your code here to use loadedImage
            	final Drawable drawable = new BitmapDrawable(getResources(), loadedImage);
            	
            }

        });*/
		//bitmap = BitmapFactory.decodeFile(path); 
		
		//Drawable drawable = new BitmapDrawable(getResources(), bitmap);
		
			
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
				intent.setClass(TortureImageViewActivity.this, ChooseDialogActivity.class);
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
				intent.setClass(TortureImageViewActivity.this, ChooseFaceActivity.class);
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
				tuyaView = new TuyaView(TortureImageViewActivity.this, content.getWidth(), content.getHeight());
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
			have_select_emoji ++;
			TortureDeleteImageView deleteImageView = new TortureDeleteImageView(this);
			LayoutParams layoutParams7 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			deleteImageView.setLayoutParams(layoutParams7);
			deleteImageView.setImageResource(R.drawable.chacha);
			
		
			final TortureDragImageView dragImageView = new TortureDragImageView(this);
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
				final TortureMyEditText editText = new TortureMyEditText(this);
				final LayoutParams layoutParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams2.gravity = Gravity.CENTER;
				editText.setBackground(null);
				editText.setHeight((int)(width1*188/1080.0));
				editText.setWidth((int)(width1*262/1080.0));
				editText.setGravity(Gravity.CENTER);
				editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
				editText.setLayoutParams(layoutParams2);
				editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(28)}); 
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
				//List<String> edited = imagesApplication.getEditedImagesPath();
				//String newfile = edited.get(index);
				String newfile = imagesApplication.getTortureImagesPath();
				if (newfile == null) newfile = Environment.getExternalStorageDirectory() + "/isayyo/temp" + Integer.toString(index+10) +Integer.toString(index+10) + ".jpg";
				File file = new File(newfile);
		        if(file.exists()){   
		            file.delete();
		        }   
		        FileOutputStream out;   
		        try {   
		            out = new FileOutputStream(file);
		            imagesApplication.setBitmap(newbitmap);
		            Bitmap resizeBmp = resizeBitmap(newbitmap);
		            if (resizeBmp.compress(Bitmap.CompressFormat.JPEG, 100, out)) {   
		                out.flush();   
		                out.close();
		                //edited.set(index, newfile);
		                imagesApplication.setTortureImagesPath(newfile);
		            }
		        } catch (FileNotFoundException e) {   
		            e.printStackTrace();   
		        } catch (IOException e) {   
		            e.printStackTrace();   
		        }
		        ahandler.sendEmptyMessage(1);
			}
		}).start();  
	}


private void TortureImageUpload(){
	getPost getpost = new getPost();
	String userid = getSharedPreferences("userInfo", Activity.MODE_PRIVATE).getString("userid", "");
	String url = "http://120.24.56.40/server/TortureImageUpload.php";
    RequestParams params1 = new RequestParams();  
    params1.put("PostId", pimgItem.getPostId());
    params1.put("UserId", userid);
    params1.put("PostImageId", pimgItem.getImageId());

	JsonHttpResponseHandler postHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode,org.apache.http.Header[] headers, JSONObject response) {
            // If the response is JSONObject instead of expected JSONArray
            String tweetText = null;
            try {
            	tweetText = response.getString("EditedImage");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Do something with the response
            //
            System.out.println(tweetText);
        
        }
        
        @Override
        public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONArray postImagePathJson) {
            // Pull out the first event on the public timeline
        	JSONObject firstEvent = null;
            try {

				JSONObject m_o = postImagePathJson.getJSONObject(0);
				TortureImageItem postItem = new TortureImageItem();
				postItem.setPostImageID(m_o.getString("PostImageID"));
				postItem.setTortureImage(m_o.getString("TortureImage"));
				postItem.setTortureImageId(m_o.getString("TortureImageID"));
				postItem.setTortureId(m_o.getString("TortureID"));
				timgItem = postItem;
				changeName();
                uploadImages();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                have_click_send_btn = false;
            }
         
            
        }
        
         @Override
         public void onFailure(int statusCode, org.apache.http.Header[] headers, String responseString, Throwable throwable) {
        	 Message msg = new Message();
				msg.what = UPLOAD_FAIL;
				msg.obj = new String("上传图片失败");
				mHandler.sendMessage(msg);
         }
   	  
   	     @Override  
   	     public void onRetry(int NO) {  
   	    	 super.onRetry(NO);
   	         // Request was retried  
   	     }   
   	  
   	     @Override  
   	     public void onFinish() {  
   	         // Completed the request (either success or failure)  
   	     } 
    };
	
    getPost.post(url, params1, postHandler);
}
//初始化倒计时器
private CountDownTimer timer = new CountDownTimer(6500, 100) {  
    @Override  
    public void onTick(long millisUntilFinished) {
    	if (sucessNumber == sumOfFile) {
    		Message msg = new Message();
    		msg.what = UPLOAD_SUCCESS;
    		mHandler.sendMessage(msg);
    	}
    }
    @Override  
    public void onFinish() {
    	Message msg = new Message();
		msg.what = UPLOAD_FAIL;
		msg.obj = new String("上传图片超时，请检查网络再重新尝试。");
		mHandler.sendMessage(msg);
    }  
}; 
private void uploadImages() {
	bucket = new OSSBucket("erzhuang");
	bucket.setBucketACL(AccessControlList.PUBLIC_READ);
	bucket.setCdnAccelerateHostId("img.isayyo.com");
	have_showed_error = false;
	
	//mProgressDialog = ProgressDialog.show(TortureImageViewActivity.this, null, "正在上传图片");
	
	pdialog.show();
	sucessNumber = 0;
	sumOfFile = 1;
	timer.start(); 
	// TODO Auto-generated method stub
		try {
				String pathString = "torphoto/"+timgItem.getTortureImage()+".jpg";
				System.out.println(pathString);
				OSSFile ossFile = new OSSFile(bucket, pathString);
				ossFile.setUploadFilePath(Environment.getExternalStorageDirectory() + "/isayyo/" + timgItem.getTortureImage() + ".jpg", "image/jpg"); // 指明需要上传的文件的路径，和文件内容类型
				ossFile.enableUploadCheckMd5sum(); // 开启上传md5校验
				tHandlers1[0] = ossFile .uploadInBackground(mSaveCallback);

				//mHandler.sendEmptyMessage(UPLOAD_SUCCESS);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Message mes = new Message();
				mes.what = UPLOAD_FAIL;
				mes.obj = new String("找不到要上传的文件");
				mHandler.sendMessage(mes);
				have_click_send_btn = false;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
}

//根据php返回的字符串，更改图片名再上传
private void changeName() {
	File file1, file2;
	
		file1 = new File(imagesApplication.getTortureImagesPath());
		System.out.println("change originalimagepath "+Environment.getExternalStorageDirectory() + "/isayyo/" + timgItem.getTortureImage() + ".jpg");
		file2 = new File(Environment.getExternalStorageDirectory() + "/isayyo/" + timgItem.getTortureImage() + ".jpg");
		file1.renameTo(file2);
}
private void initTitleBar() {
	if (getIntent().getBooleanExtra("is_forward", false)) {
		layout3.setBackgroundRes(R.drawable.nav);
		layout4.setBackgroundRes(R.drawable.nav);
		layout3.setTitleText("转发");
		layout4.setTitleText("转发");
		layout3.setLeftButtonIcon(this, R.drawable.cancel_selector);
		layout3.setRightButtonIcon(this, R.drawable.next1_selector);
		layout4.setLeftButtonIcon(this, R.drawable.back_btn_selector);
		layout4.setRightButtonIcon(this, R.drawable.fok_selector);
		layout3.setTitleTextColor(0xff000000);
		layout4.setTitleTextColor(0xff000000);
	}
	else {
		layout3.setTitleText("吐槽");
		layout4.setTitleText("涂鸦");
		layout3.setLeftButtonIcon(this, R.drawable.cancel);
		layout3.setRightButtonIcon(this, R.drawable.next_selector);
		layout4.setLeftButtonIcon(this, R.drawable.backw_selector);
		layout4.setRightButtonIcon(this, R.drawable.ok_selector);
		layout3.setTitleTextColor(0xffffffff);
		layout4.setTitleTextColor(0xffffffff);
	}
	layout3.setLeftBtnClickable(true);
	layout3.setRightButtonVISIBLE(true);
	layout3.setCenterTextVISIBLE(false);
	layout3.setLeftButtonBackgroundNULL(this);
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
						finish();
					}
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(TortureImageViewActivity.this)
					.setMessage("是否放弃此次编辑？")
					.setPositiveButton("确定", listener)
					.setNegativeButton("取消", null);
				builder.create()
					.show();
			} else {
				finish();
			}
		}
	});
	//吐槽topbar的next函数
	layout3.setRightButtonClickListener(new OnTitleRightButtonClickListener() {
		
		@Override
		public void onRightButtonClick(View v) {
			// TODO Auto-generated method stub
			String a  = "a " + have_select_emoji;
			Log.e("first button",a);
			boolean flag = true;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getText().toString().equals("")) {
					flag = false;
					break;
				}
			}
			if (flag) {
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
				tuyaView = new TuyaView(TortureImageViewActivity.this, content.getWidth(), content.getHeight());
				tuyaView.setColor(getResources().getColor(R.color.color1));
				tuyaView.setPaintWidth(20);
				
				content.addView(tuyaView);
			} else {
				Toast.makeText(TortureImageViewActivity.this, "似乎没有吐槽啊", Toast.LENGTH_SHORT).show();;
			}
		}
	});
	layout4.setLeftBtnClickable(true);
	layout4.setRightButtonVISIBLE(true);
	layout4.setCenterTextVISIBLE(false);
	layout4.setLeftButtonBackgroundNULL(this);
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
				AlertDialog.Builder builder = new AlertDialog.Builder(TortureImageViewActivity.this)
					.setMessage("是否放弃编辑画板？")
					.setPositiveButton("确定", listener)
					.setNegativeButton("取消", null);
				builder.create()
					.show();
			} else {
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
			
			if(tuyaView.isDirty  ||have_select_emoji >0){
			// TODO Auto-generated method stub
			layout4.setRightButtonVISIBLE(false);
			pdialog = CustomProgressDialog.createDialog(TortureImageViewActivity.this);
			
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
			//mProgressDialog = ProgressDialog.show(TortureImageViewActivity.this, null, "正在保存更改后的图片");
			saveBitmap();
		}
		 else {
			Toast.makeText(getApplicationContext(), "似乎没有吐槽哦! 亲", Toast.LENGTH_SHORT).show();
		}
		}
	});
	
}
}

	//发送图片的张数，uid和comment，获取图片的名字
/*	
private void ConnectedWeb() {
		String url = AppConstant.postImagePath;
		new Thread() {
			public void run() {
				ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				nameValuePair.add(new BasicNameValuePair("comment", comment.getText().toString()));
				nameValuePair.add(new BasicNameValuePair("RowCount", Integer.toString(imagesPath.size())));
				nameValuePair.add(new BasicNameValuePair("uid", getSharedPreferences("userInfo", Activity.MODE_PRIVATE).getString("userid", "")));
				Message msg = new Message();
				InputStream in = null;
				msg.what = WEB_RESPONSE;
				// 链接网页
				try {
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(url);
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
					HttpResponse response = httpClient.execute(httpPost);
					msg.arg1 = response.getStatusLine().getStatusCode();
					in = response.getEntity().getContent();
				} catch (Exception e) {
					// TODO: handle exception
					// Toast.makeText(LoginActivity.this, "连接失败",
					// Toast.LENGTH_LONG).show();
				}
				// 处理结果
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String line1 = null;
					String line = null;
					while ((line = br.readLine()) != null) {
						line1 = line;
					}
					in.close();
					msg.obj = line1;
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}.start();
	}

private void uploadImages() {
	bucket = new OSSBucket("erzhuang");
	bucket.setBucketACL(AccessControlList.PUBLIC_READ);
	bucket.setCdnAccelerateHostId("img.isayyo.com");
	changeName();
	mProgressDialog = ProgressDialog.show(PublishActivity.this, null, "正在上传图片");
	new Thread(new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				for (int i = 0; i < imagesPath.size(); i++) {
					OSSFile ossFile = new OSSFile(bucket, "orgphoto/"+originalImage.get(i)+".jpg");
					ossFile.setUploadFilePath(imagesPath.get(i), "image/jpg"); // 指明需要上传的文件的路径，和文件内容类型
					ossFile.enableUploadCheckMd5sum(); // 开启上传md5校验
					ossFile.upload(); // 上传失败将会抛出异常
				}

				mHandler.sendEmptyMessage(UPLOAD_SUCCESS);
			} catch (OSSException ossException) {
				// TODO: handle exception
				if (ossException.getExceptionType() == ExceptionType.OSS_EXCEPTION) {
					String objectKey = ossException.getObjectKey(); // 获取该任务对应的ObjectKey
					String mesString = ossException.getMessage(); // 异常信息
					String info = ossException.toString();
					Exception localException = ossException.getException(); // 取得原始的异常
					ossException.printStackTrace(); // 打印栈
				}
				Message mes = new Message();
				mes.what = UPLOAD_FAIL;
				mes.obj = new String("上传图片出错");
				mHandler.sendMessage(mes);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Message mes = new Message();
				mes.what = UPLOAD_FAIL;
				mes.obj = new String("找不到要上传的文件");
				mHandler.sendMessage(mes);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}).start();
}*/
