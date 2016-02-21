package com.isayyo.app.editimg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.isayyp.app.widght.CustomProgressDialog;
import cn.jpush.android.ui.PushActivity;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.statistics.NewAppReceiver;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.smssdk.framework.utils.UIHandler;

import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.model.AccessControlList;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.model.OSSException.ExceptionType;
import com.alibaba.sdk.android.oss.model.TokenGenerator;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.alibaba.sdk.android.oss.storage.TaskHandler;
import com.alibaba.sdk.android.oss.util.OSSToolKit;
import com.isayyo.app.AppConstant;
import com.isayyo.app.R;
import com.isayyo.app.SelectImagesTopBar;
import com.isayyo.app.ShareActivity;
import com.isayyo.app.SelectImagesTopBar.OnTitleLeftButtonClickListener;
import com.isayyo.app.SelectImagesTopBar.OnTitleRightButtonClickListener;
import com.isayyo.app.cropimage.CropPictureActivity;
import com.isayyo.app.utils.SmartBarUtils;
import com.isayyo.push.DemoApplication;
import com.issayo.app.entity.PostImageItem;
import com.issayo.app.entity.PostItem;

public class PublishActivity extends Activity implements PlatformActionListener, Callback{
	private final static int WEB_RESPONSE = 0x00;
	private final static int UPLOAD_SUCCESS = 0x01;
	private final static int UPLOAD_FAIL = 0x02;
	private static final int MSG_TOAST = 0x03;
	private static final int MSG_ACTION_CCALLBACK = 0x04;
	private static final int MSG_CANCEL_NOTIFY = 0x05;
	private static final String accessKey = "msbTOIO0FBOwFBNG"; // 测试代码没有考虑AK/SK的安全性
	private static final String screctKey = "JnhKdvE9xlZSuHKjkdloKDNOhnwqqm";
	private static CustomProgressDialog pdialog;
//	private ProgressDialog mProgressDialog;
	private ImageView weibo, wechatMoments, qqzone, wechat;
	private SelectImagesTopBar topbar;
	private int currentSelectedToShare = 0;//记录选择了同步分享的平台，0表示什么都没选
	private String url;
	private OSSBucket bucket;
	//动态加载的展示图片的imageview
	private ImageView[] img;
	private Bitmap[] bitmaps;
	private EditText comment;
	public PostItem mPostItem;
	//全局变量
	private DemoApplication imagesApp;
	private LinearLayout imgsContainer; 
	private List<String> imagesPath, editedImagesPath;
	private List<String> originalImage, afterEditImage;
	//标记是否已经发现上传出错
	private boolean have_showed_error, have_click_send_btn = false; 
	//上传成功的个数和要上传文件的总数
	private int sucessNumber, sumOfFile;
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
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what == WEB_RESPONSE) {
				switch (msg.arg1) {
				case 404:
					have_click_send_btn = false; 
					Toast.makeText(PublishActivity.this, "服务器出错",Toast.LENGTH_SHORT).show();
					break;
				case 200:
					String result = (String)msg.obj;
					Log.i("mytest", result);
					try {
						JSONObject jso = new JSONObject(result);
						//JSONObject postid = jso.getJSONObject("postid");
						JSONArray imgid = jso.getJSONArray("imgid");
						JSONArray original = jso.getJSONArray("orgarray");
						JSONArray afteredit = jso.getJSONArray("iconarray");
						originalImage = new ArrayList<String>();
						afterEditImage = new ArrayList<String>();
						mPostItem = new PostItem();
						mPostItem.setText(comment.getText().toString());
						mPostItem.setNoOfLike("0");
						mPostItem.setCommentCount("0");
						mPostItem.setType("0");
						String userid = PublishActivity.this.getSharedPreferences("userInfo", Activity.MODE_PRIVATE).getString("userid", "");
						mPostItem.setUserID(userid);
						Time today = new Time(Time.getCurrentTimezone());
						today.setToNow();
						mPostItem.setTimeCreated(today.year + "-" + today.month + "-" + today.monthDay + " " + today.format("%k:%M:%S"));
						System.out.println(jso.getString("postid"));
						mPostItem.setPostId(jso.getString("postid"));
						ArrayList<PostImageItem> pImgArrayList = new ArrayList<PostImageItem>();
						for (int i = 0; i < original.length(); i++) {
							PostImageItem postImgItem = new PostImageItem();
							System.out.println("uploadimage original path "+original.getString(i));
							postImgItem.setOriginalImage(original.getString(i));
							pImgArrayList.add(postImgItem);
							originalImage.add(original.getString(i));
						}
						for (int i = 0; i < afteredit.length(); i++) {
							PostImageItem postImgItem = pImgArrayList.get(i);
							System.out.println("uploadimage edited path "+afteredit.getString(i));
							postImgItem.setEditedImage(afteredit.getString(i));
							afterEditImage.add(afteredit.getString(i));
						}
						for (int i = 0; i < imgid.length(); i++) {
							PostImageItem postImgItem = pImgArrayList.get(i);
							postImgItem.setImageId(imgid.getString(i));
							postImgItem.setPostId(mPostItem.getPostId());
							//afterEditImage.add(imgid.getString(i));
						}
						mPostItem.setPostImageArray(pImgArrayList);
						uploadImages();
					} catch (JSONException e) {
						// TODO: handle exception
						e.printStackTrace();
						have_click_send_btn = false;
					}
					break;
				default:
					have_click_send_btn = false;
					Toast.makeText(PublishActivity.this, "服务器出错",Toast.LENGTH_SHORT).show();
					break;
				}
			}
			else if (msg.what == UPLOAD_SUCCESS) {
				pdialog.dismiss();
//				mProgressDialog.dismiss();
				//停止倒计时器
				timer.cancel();
				//显示上传成功
				share();
				have_click_send_btn = false;
			}
			else if (msg.what == UPLOAD_FAIL) {
				have_click_send_btn = false;
				if (have_showed_error == false) {
//					mProgressDialog.dismiss();
					pdialog.dismiss();
					//停止倒计时器
					timer.cancel();
					//停止所有其他任务
					for (int i = 0; i < imagesPath.size(); i++) {
						if (tHandlers1[i] != null) tHandlers1[i].cancel();
						if (tHandlers2[i] != null) tHandlers2[i].cancel();
					}
					//显示错误信息
					String info = (String)msg.obj;
					Toast.makeText(PublishActivity.this, info,Toast.LENGTH_SHORT).show();
					//删除数据库的记录
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
							nameValuePair.add(new BasicNameValuePair("pid", mPostItem.getPostId()));
							// 链接网页
							try {
								HttpClient httpClient = new DefaultHttpClient();
								HttpPost httpPost = new HttpPost(AppConstant.delePostPath);
								httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
								httpClient.execute(httpPost);
							} catch (Exception e) {
								
							}
						}
					}).start();
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish);
		SmartBarUtils.hide(getWindow().getDecorView());
		OSSClient.setApplicationContext(getApplicationContext());
		ShareSDK.initSDK(this);
		initview();
		initTitleBar();
		initevent();
	}
	private void initview() {
		weibo = (ImageView)findViewById(R.id.publish_share_to_weibo);
		wechat = (ImageView)findViewById(R.id.publish_share_to_wechat);
		wechatMoments = (ImageView)findViewById(R.id.publish_share_to_wechatmoment);
		qqzone = (ImageView)findViewById(R.id.publish_share_to_qqzone);
		topbar = (SelectImagesTopBar)findViewById(R.id.publish_top_bar);
		imgsContainer = (LinearLayout)findViewById(R.id.publish_imgs);
		imagesApp = (DemoApplication)getApplication();
		comment = (EditText)findViewById(R.id.publish_comment);
		//设置输入监听器
		comment.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int start;
			private int end;
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				temp = s;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				start = comment.getSelectionStart();
				end = comment.getSelectionEnd();
				 if (temp.length() > 15) {  
	                Toast.makeText(PublishActivity.this,  
	                        "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT)  
	                        .show();  
	                s.delete(start - 1, end);  
	                int tempSelection = start;  
	                comment.setText(s);  
	                comment.setSelection(tempSelection);  
		         } 
			}
		});
		imagesPath = imagesApp.getImagesPath();
		editedImagesPath = imagesApp.getEditedImagesPath();
		initimages();
	}
	private void initimages() {
		bitmaps = new Bitmap[4];
		img = new ImageView[4];
		int lh = dip2px(PublishActivity.this, 68);
		DisplayMetrics  dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);   
		int lw = dm.widthPixels - dip2px(PublishActivity.this, 30);
		int margin = (lw - 4 * lh)/3;
		//加图
		imagesApp.postBmpArrayList.clear();
		for (int i = 0; i < imagesPath.size(); i++) {
			img[i] = new  ImageView(PublishActivity.this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(lh, lh);
			if (i != 3) {
				lp.setMargins(0, 0, margin, 0);
			}
			img[i].setLayoutParams(lp);
			if (editedImagesPath.get(i) == "") {
				bitmaps[i] = getCompressedBitmap(imagesPath.get(i));
				imagesApp.postBmpArrayList.add(bitmaps[i]);
				img[i].setImageBitmap(bitmaps[i]);
			}
			else {
				bitmaps[i] = getCompressedBitmap(editedImagesPath.get(i));
				imagesApp.postBmpArrayList.add(bitmaps[i]);
				img[i].setImageBitmap(bitmaps[i]);
			}
			img[i].setBackgroundColor(0xffe6e6e6);
			img[i].setScaleType(ScaleType.FIT_CENTER);
			imgsContainer.addView(img[i]);
		}
	}
	public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
	private void initevent() {
		weibo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (weibo.isSelected()) {
					weibo.setSelected(false);
					currentSelectedToShare = 0;
				} else {
					weibo.setSelected(true);
					currentSelectedToShare = 3;
					wechatMoments.setSelected(false);
					qqzone.setSelected(false);
					wechat.setSelected(false);
				}
			}
		});
		wechatMoments.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (wechatMoments.isSelected()) {
					wechatMoments.setSelected(false);
					currentSelectedToShare = 0;
				} else {
					wechatMoments.setSelected(true);
					currentSelectedToShare = 1;
					weibo.setSelected(false);
					qqzone.setSelected(false);
					wechat.setSelected(false);
				}
			}
		});
		qqzone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (qqzone.isSelected()) {
					qqzone.setSelected(false);
					currentSelectedToShare = 0;
				} else {
					qqzone.setSelected(true);
					currentSelectedToShare = 2;
					weibo.setSelected(false);
					wechatMoments.setSelected(false);
					wechat.setSelected(false);
				}
			}
		});
		wechat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (wechat.isSelected()) {
					wechat.setSelected(false);
					currentSelectedToShare = 0;
				} else {
					wechat.setSelected(true);
					currentSelectedToShare = 4;
					weibo.setSelected(false);
					wechatMoments.setSelected(false);
					qqzone.setSelected(false);
				}
			}
		});
	}
	//发送图片的张数，uid和comment，获取图片的名字
	private void ConnectedWeb() {
		url = AppConstant.postImagePath;
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
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair,HTTP.UTF_8));
					HttpResponse response = httpClient.execute(httpPost);
					msg.arg1 = response.getStatusLine().getStatusCode();
					in = response.getEntity().getContent();
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(PublishActivity.this, "连接网络失败",Toast.LENGTH_LONG).show();
					have_click_send_btn = false;
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
					have_click_send_btn = false;
				}
			}
		}.start();
	}
	private void uploadImages() {
		bucket = new OSSBucket("erzhuang");
		bucket.setBucketACL(AccessControlList.PUBLIC_READ);
		bucket.setCdnAccelerateHostId("img.isayyo.com");
		changeName();
		have_showed_error = false;
		pdialog = CustomProgressDialog.createDialog(PublishActivity.this);
		pdialog.show();
//		mProgressDialog = ProgressDialog.show(PublishActivity.this, null, "正在上传图片");
		sucessNumber = 0;
		sumOfFile = imagesPath.size() * 2;
		timer.start(); 
		try {
			for (int i = 0; i < imagesPath.size(); i++) {
				OSSFile ossFile = new OSSFile(bucket, "orgphoto/"+originalImage.get(i)+".jpg");
				ossFile .setUploadFilePath(imagesPath.get(i), "image/jpg"); // 指明需要上传的文件的路径，和文件内容类型
				ossFile .enableUploadCheckMd5sum(); // 开启上传md5校验
				tHandlers1[i] = ossFile .uploadInBackground(mSaveCallback);
			}
			for (int i = 0; i < imagesPath.size(); i++) {
				OSSFile ossFile = new OSSFile(bucket, "ediphoto/"+afterEditImage.get(i)+".jpg");
				ossFile.setUploadFilePath(editedImagesPath.get(i), "image/jpg"); // 指明需要上传的文件的路径，和文件内容类型
				ossFile.enableUploadCheckMd5sum(); // 开启上传md5校验
				tHandlers2[i] = ossFile.uploadInBackground(mSaveCallback);
			}
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			Message msg = new Message();
			msg.what = UPLOAD_FAIL;
			msg.obj = new String("找不到上传文件");
			mHandler.sendMessage(msg);
			have_click_send_btn = false;
		}
			
	}
	//根据php返回的字符串，更改图片名再上传
	private void changeName() {
		File file1, file2;
		for (int i = 0; i < imagesPath.size(); i++) {
			file1 = new File(imagesPath.get(i));
			System.out.println("change originalimagepath "+Environment.getExternalStorageDirectory() + "/isayyo/" + originalImage.get(i) + ".jpg");
			file2 = new File(Environment.getExternalStorageDirectory() + "/isayyo/" + originalImage.get(i) + ".jpg");
			file1.renameTo(file2);
			imagesPath.set(i, file2.getAbsolutePath());
		}
		for (int i = 0; i < imagesPath.size(); i++) {
			if (editedImagesPath.get(i) != "") {
				file1 = new File(editedImagesPath.get(i));
				file2 = new File(Environment.getExternalStorageDirectory() + "/isayyo/" + afterEditImage.get(i) + ".jpg");
				System.out.println("change editedimagepath "+Environment.getExternalStorageDirectory() + "/isayyo/" + afterEditImage.get(i) + ".jpg");
				file1.renameTo(file2);
				editedImagesPath.set(i, file2.getAbsolutePath());
			} else {
				file1 = new File(imagesPath.get(i));
				file2 = new File(Environment.getExternalStorageDirectory() + "/isayyo/" + afterEditImage.get(i) + ".jpg");
				try {
					FileInputStream fosfrom = new FileInputStream(file1);  
		            FileOutputStream fosto = new FileOutputStream(file2);  
		            byte[] bt = new byte[1024];  
		            int c;  
		            while((c=fosfrom.read(bt)) > 0){  
		                fosto.write(bt,0,c);  
		            }  
		            //关闭输入、输出流  
		            fosfrom.close();  
		            fosto.close();
		            editedImagesPath.set(i, file2.getAbsolutePath());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		imagesApp.setImagesPath(imagesPath);
		imagesApp.setEditedImagesPath(editedImagesPath);
	}
	private void share() {
		//初始化分享的url
		String url = "http://www.isayyo.com/sharenew.html?picid=ediphoto/"
					+ afterEditImage.get(0) + ".jpg";
		OnekeyShare oks = new OnekeyShare();
		Bundle bundle = new Bundle();
		bundle.putSerializable("NewPost", mPostItem);
		Intent returnIntent = new Intent();
		returnIntent.putExtras(bundle);
		switch (currentSelectedToShare) {

			case 1://微信朋友圈
				ShareParams wechatMoments = new ShareParams();
				wechatMoments.setTitle("分享晒友");
				wechatMoments.setText("我在晒友里面看到了你的照片，这张照片不是我发的哟！");
				wechatMoments.setUrl(url);
				wechatMoments.setImagePath(editedImagesPath.get(0));
				wechatMoments.setShareType(Platform.SHARE_WEBPAGE);
				Platform weixin = ShareSDK.getPlatform(PublishActivity.this,
						WechatMoments.NAME);
				weixin.setPlatformActionListener(PublishActivity.this);
				weixin.share(wechatMoments);
				break;
			case 2://QQ空间
				oks.disableSSOWhenAuthorize();
				oks.setPlatform(QZone.NAME);
				oks.setTitle("分享晒友");
				oks.setTitleUrl(url);
				oks.setText("我在晒友里面看到了你的照片，这张照片不是我发的哟！");
				oks.setImagePath(editedImagesPath.get(0));
				oks.setSite("晒友");
				oks.setSiteUrl("http://www.isayyo.com");
				oks.setSilent(true);
				oks.show(PublishActivity.this);
				PublishActivity.this.setResult(112,returnIntent);
				PublishActivity.this.finish();
				break;
			case 3://微博
				oks.setPlatform(SinaWeibo.NAME);
				oks.setText("我在晒友里面看到了你的照片，这张照片不是我发的哟！" + url);
				oks.setImagePath(editedImagesPath.get(0));
				oks.setSilent(true);
				oks.show(PublishActivity.this);
				PublishActivity.this.setResult(112,returnIntent);
				PublishActivity.this.finish();
				break;
			case 4://微信好友
				ShareParams wechat = new ShareParams();
				wechat.setTitle("分享晒友");
				wechat.setText("我在晒友里面看到了你的照片，这张照片不是我发的哟！");
				wechat.setImagePath(editedImagesPath.get(0));
				wechat.setUrl(url);
				wechat.setShareType(Platform.SHARE_WEBPAGE);
				Platform weixin1 = ShareSDK.getPlatform(PublishActivity.this,
						Wechat.NAME);
				weixin1.setPlatformActionListener(PublishActivity.this);
				weixin1.share(wechat);
				PublishActivity.this.setResult(112,returnIntent);
				PublishActivity.this.finish();
				break;
			case 0://什么平台都没选，直接退出

				PublishActivity.this.setResult(112,returnIntent);
				PublishActivity.this.finish();
				break;
			default:
				break;
		}
	}
	private Bitmap getCompressedBitmap(String path) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options(); 
		newOpts.inJustDecodeBounds = true; 
		System.out.println("Get compressed bitmap " + path);
        Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);   
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        int hh = 150;
        int ww = 150; 
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
//        Log.i("mytest", "size is:" + bitmap.getByteCount());
//        Log.i("mytest", "w is:" + bitmap.getWidth() + "\n" + "h is:" + bitmap.getHeight());
        return bitmap; 
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		for (int i = 0; i < imagesPath.size(); i++) {
			if (bitmaps[i] != null) {
				bitmaps[i].recycle();
				bitmaps[i] = null;
			}
		}
		System.gc();
		super.onDestroy();
		ShareSDK.stopSDK(this);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.publish, menu);
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
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		bundle.putSerializable("NewPost", mPostItem);
		Intent returnIntent = new Intent();
		returnIntent.putExtras(bundle);
		switch (msg.what) {
		case MSG_TOAST: {
			String text = String.valueOf(msg.obj);
			Toast.makeText(PublishActivity.this, text, Toast.LENGTH_SHORT).show();
		}
			break;
		case MSG_ACTION_CCALLBACK: {
			switch (msg.arg1) {
			case 1: { // 成功, successful notification
				showNotification(2000, getString(R.string.share_completed));
				PublishActivity.this.setResult(112,returnIntent);
				PublishActivity.this.finish();
			}
				break;
			case 2: { // 失败, fail notification
				String expName = msg.obj.getClass().getSimpleName();
				if ("WechatClientNotExistException".equals(expName)
						|| "WechatTimelineNotSupportedException"
								.equals(expName)) {
					showNotification(2000,
							getString(R.string.wechat_client_inavailable));
				} else if ("GooglePlusClientNotExistException".equals(expName)) {
					showNotification(2000,
							getString(R.string.google_plus_client_inavailable));
				} else if ("QQClientNotExistException".equals(expName)) {
					showNotification(2000,
							getString(R.string.qq_client_inavailable));
				} else {
					showNotification(2000, getString(R.string.share_failed));
				}
				PublishActivity.this.setResult(112,returnIntent);
				PublishActivity.this.finish();
			}
				break;
			case 3: { // 取消, cancel notification
				showNotification(2000, getString(R.string.share_canceled));
				PublishActivity.this.setResult(112,returnIntent);
				PublishActivity.this.finish();
			}
				break;
			}
		}
			break;
		case MSG_CANCEL_NOTIFY: {
			NotificationManager nm = (NotificationManager) msg.obj;
			if (nm != null) {
				nm.cancel(msg.arg1);
			}
		}
			break;
		}
		return false;
	}
	private void showNotification(long cancelTime, String text) {
		try {
			Context app = getApplicationContext();
			NotificationManager nm = (NotificationManager) app
					.getSystemService(Context.NOTIFICATION_SERVICE);
			final int id = Integer.MAX_VALUE / 13 + 1;
			nm.cancel(id);

			long when = System.currentTimeMillis();
			Notification notification = new Notification(R.drawable.launcher,
					text, when);
			PendingIntent pi = PendingIntent.getActivity(app, 0, new Intent(),
					0);
			notification.setLatestEventInfo(app, "sharesdk test", text, pi);
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			nm.notify(id, notification);

			if (cancelTime > 0) {
				Message msg = new Message();
				msg.what = MSG_CANCEL_NOTIFY;
				msg.obj = nm;
				msg.arg1 = id;
				UIHandler.sendMessageDelayed(msg, cancelTime, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onCancel(Platform arg0, int arg1) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 3;
		msg.arg2 = arg1;
		msg.obj = arg0;
		UIHandler.sendMessage(msg, this);
	}
	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 1;
		msg.arg2 = arg1;
		msg.obj = arg0;
		UIHandler.sendMessage(msg, this);
	}
	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		arg2.printStackTrace();
		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 2;
		msg.arg2 = arg1;
		msg.obj = arg2;
		UIHandler.sendMessage(msg, this);
	}
	private void initTitleBar() {
		if (getIntent().getBooleanExtra("is_forward", false)) {
			topbar.setBackgroundRes(R.drawable.nav);
			topbar.setLeftButtonIcon(this, R.drawable.back_btn_selector);
			topbar.setRightButtonIcon(this, R.drawable.fsend_selector);
			topbar.setTitleText("转发");
			topbar.setTitleTextColor(0xff000000);
		}
		else {
			topbar.setLeftButtonIcon(this, R.drawable.back1_selector);
			topbar.setRightButtonIcon(this, R.drawable.send_btn_selector);
			topbar.setTitleText("发布");
			topbar.setTitleTextColor(0xffffffff);
		}
		topbar.setLeftBtnClickable(true);
		topbar.setRightButtonVISIBLE(true);
		topbar.setCenterTextVISIBLE(false);
		topbar.setLeftButtonBackgroundNULL(this);
		topbar.setLeftButtonClickListener(new OnTitleLeftButtonClickListener() {
			@Override
			public void onLeftButtonClick(View v) {
				// TODO Auto-generated method stub
				//如果只有一张图，会退回到重新编辑页面，所以要询问是否重新编辑
				if (imagesPath.size() == 1) {
					android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							PublishActivity.this.setResult(110); 
							finish();
						}
					};
					AlertDialog.Builder builder = new AlertDialog.Builder(PublishActivity.this)
						.setMessage("是否重新编辑图片？")
						.setPositiveButton("确定", listener)
						.setNegativeButton("取消", null);
					builder.create()
						.show();
				}
				//如果多于一张图，就会退回发布前页面，此时不用询问
				else {
					PublishActivity.this.setResult(110); 
					finish();
				}
			}
		});
		topbar.setRightButtonClickListener(new OnTitleRightButtonClickListener() {
			
			@Override
			public void onRightButtonClick(View v) {
				// TODO Auto-generated method stub
				if (!have_click_send_btn) {
					have_click_send_btn = true;
					ConnectedWeb();
				}		
			}
		});
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//如果只有一张图，会退回到重新编辑页面，所以要询问是否重新编辑
		if (imagesPath.size() == 1) {
			android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					PublishActivity.this.setResult(110); 
					finish();
				}
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(PublishActivity.this);
			builder.setMessage("是否重新编辑图片？");
			builder.setPositiveButton("确定", listener);
			builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
				
			});
			builder.create()
				.show();
		}
		//如果多于一张图，就会退回发布前页面，此时不用询问
		else {
			PublishActivity.this.setResult(110); 
			finish();
		}
	}
}
