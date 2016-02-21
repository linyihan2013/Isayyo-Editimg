package com.isayyo.app.editimg;

import com.avos.avoscloud.LogUtil.log;

import android.R.bool;
import android.R.integer;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ZoomImageView extends ImageView {

	float x_down = 0;
	float y_down = 0;
	public static float scale = 1;
	float scale1 = 1;
	int width = 0;
	int height = 0;

	PointF start = new PointF();
	PointF mid = new PointF();

	float oldDist = 1f;
	float oldRotation = 0;
	float newDist = 1f;
	float newRotation = 0;
	Matrix matrix2 = new Matrix();
	Matrix matrix1 = new Matrix();
	Matrix savedMatrix = new Matrix();
	boolean isDirty = false;

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	int mode = NONE;
	private int moveCount;
	boolean matrixCheck = false;

	private final String TAG = "TouchImageView";

	public ZoomImageView(Context context) {
		super(context);
	}

	/**
	 * 该构造方法在静态引入XML文件中是必须的
	 * 
	 * @param context
	 * @param paramAttributeSet
	 */
	public ZoomImageView(Context context, AttributeSet paramAttributeSet) {
		super(context, paramAttributeSet);
		moveCount = 0;
	}

	public boolean onTouchEvent(MotionEvent event) {
		FrameLayout layout = (FrameLayout)this.getParent();
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			Log.e("ZoomImage", "Action Down");
			mode = DRAG;
			moveCount = 0;
			if (isDirty == false) {
				oldDist = spacing(event);
				oldRotation = rotation(event);
				isDirty = true;
			}
			savedMatrix.set(matrix2);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			/*mode = ZOOM;
			oldDist = spacing(event);
			oldRotation = rotation(event);
			savedMatrix.set(matrix2);
			midPoint(mid, event);
			break;*/
		case MotionEvent.ACTION_MOVE:
			
			/*if (mode == ZOOM) {
				matrix1.set(savedMatrix);
				float rotation = rotation(event) - oldRotation;
				float newDist = spacing(event);
				float scale = newDist / oldDist;
				matrix1.postScale(scale, scale, mid.x, mid.y);// 縮放
				matrix1.postRotate(rotation, mid.x, mid.y);// 旋轉
				// matrixCheck = matrixCheck();
				if (matrixCheck == false) {
					matrix2.set(matrix1);
					invalidate();
				}
			} else */if (mode == DRAG & moveCount > 1) {
				matrix1.set(savedMatrix);
				newRotation = rotation(event) - oldRotation;
				newDist = spacing(event);
				scale1 = newDist / oldDist;
				scale = (float) (scale1*1);
				matrix1.postScale(scale1, scale1, mid.x, mid.y);// 縮放
				Log.e("ZoomImage", "Scale" + scale + "newdist" +newDist + "oldDist"+oldDist);
				if (scale > 0.5) {
					layout.setScaleX(scale);
					layout.setScaleY(scale);
				}
				
				layout.setRotation((float)1.5*(newRotation));
				layout.invalidate();
				if (matrixCheck == false) {
					matrix2.set(matrix1);
					invalidate();
				}
			}
			moveCount = moveCount + 1;
			break;
		case MotionEvent.ACTION_UP:
			Log.e("ZoomImage", "Action Up");
			moveCount = 0;
			mode = NONE;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			Log.e("ZoomImage", "Action Pointer Up");
			mode = NONE;
			break;
		}
		
		return true;
	}

	// 触碰两点间距离
	private float spacing(MotionEvent event) {
		FrameLayout layout = (FrameLayout)this.getParent();
		float x = event.getRawX()-layout.getX()-layout.getWidth()/2;
		float y = event.getRawY()-layout.getY()-layout.getHeight()/2;
		return FloatMath.sqrt(x * x + y * y);
	}

	// 取手势中心点
	private void midPoint(PointF point, MotionEvent event) {
		FrameLayout layout = (FrameLayout)this.getParent();
		point.set(layout.getX()+layout.getWidth()/2, layout.getY()+layout.getHeight()/2);
	}

	// 取旋转角度
	private float rotation(MotionEvent event) {
		FrameLayout layout = (FrameLayout)this.getParent();
		double delta_x = event.getRawX()-layout.getX()-layout.getWidth()/2;
		double delta_y = event.getRawY()-layout.getY()-layout.getHeight()/2;
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

	// 将移动，缩放以及旋转后的图层保存为新图片
	// 本例中沒有用到該方法，需要保存圖片的可以參考
	// public Bitmap CreatNewPhoto() {
	// Bitmap bitmap = Bitmap.createBitmap(widthScreen, heightScreen,
	// Config.ARGB_8888); // 背景图片
	// Canvas canvas = new Canvas(bitmap); // 新建画布
	// canvas.drawBitmap(gintama, matrix, null); // 画图片
	// canvas.save(Canvas.ALL_SAVE_FLAG); // 保存画布
	// canvas.restore();
	// return bitmap;
	// }

}
