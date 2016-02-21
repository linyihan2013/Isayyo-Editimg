package com.isayyo.app.editimg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.isayyo.app.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HuaBanView extends View {

	/**缓冲位图*/
	private Bitmap cacheBitmap;
	/**缓冲位图的画板*/
	private Canvas cacheCanvas;
	/**缓冲画笔*/
	private Paint paint;
	/**实际画笔*/
	private Paint BitmapPaint;
	/**保存绘制曲线路径*/
	private Path path;
	/**画布高*/
	private int height;
	/**画布宽*/
	private int width;
	
	private boolean ability = true;
	
	/**保存上一次绘制的终点横坐标*/
	private float pX;
	/**保存上一次绘制的终点纵坐标*/
	private float pY;
	
	private static List<DrawPath> savePath;
	
	private DrawPath dp;
	
	private class DrawPath {
		  public Path path;// 路径
		  public Paint paint;// 画笔
	}
	/**画笔初始颜色*/
	private int paintColor = R.color.color1;
	/**线状状态*/
	private static Paint.Style paintStyle = Paint.Style.STROKE;
	/**画笔粗细*/
	private static int paintWidth = 20;
	
	private Canvas canvas;
	
	public void setAbility(boolean state) {
		ability = state;
	}
	
	/**获取View实际宽高的方法*/
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		height = h;
		width = w;
		init();
	}
	
	private void init(){
		cacheBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		cacheCanvas = new Canvas(cacheBitmap);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStrokeCap(Paint.Cap.ROUND);
		path = new Path();
		BitmapPaint = new Paint(Paint.DITHER_FLAG);
		savePath = new ArrayList<DrawPath>();
		updatePaint();
	}
	
	private void updatePaint(){
		paint.setColor(paintColor);
		paint.setStyle(paintStyle);
		paint.setStrokeWidth(paintWidth);
	}
	
	public HuaBanView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public HuaBanView(Context context){
		super(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (ability) {
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				path = new Path();
				dp = new DrawPath();
				dp.path = path;
				dp.paint = paint;
				path.moveTo(event.getX(), event.getY());
				pX = event.getX();
				pY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				path.quadTo(pX, pY, event.getX(), event.getY());
				pX = event.getX();
				pY = event.getY();
				break;
			case MotionEvent.ACTION_UP:
				path.lineTo(pX, pY);
				cacheCanvas.drawPath(path, paint);
				savePath.add(dp);
				path.reset();
				break;
			}
			invalidate();
		}
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
			this.canvas = canvas;
			canvas.drawBitmap(cacheBitmap, 0,0, BitmapPaint);
			if (path != null) {
				canvas.drawPath(path, paint);
			}
	}
	
	/**更新画笔颜色*/
	public void setColor(int color){
		paintColor = color;
		updatePaint();
	}
	
	/**设置画笔粗细*/
	public void setPaintWidth(int width){
		paintWidth = width;
		updatePaint();
	}
	
	public static final int PEN = 1;
	public static final int PAIL = 2;
	
	/**设置画笔样式*/
	public void setStyle(int style){
		switch(style){
		case PEN:
			paintStyle = Paint.Style.STROKE;
			break;
		case PAIL:
			paintStyle = Paint.Style.FILL;
			break;
		}
		updatePaint();
	}
	
	public void undo() {
		cacheBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		cacheCanvas.setBitmap(cacheBitmap);// 重新设置画布，相当于清空画布
		  // 清空画布，但是如果图片有背景的话，则使用上面的重新初始化的方法，用该方法会将背景清空掉...
		 if (savePath != null && savePath.size() > 0) {
			 // 移除最后一个path,相当于出栈操作
			 savePath.remove(savePath.size() - 1);
			 Iterator<DrawPath> iter = savePath.iterator();
			 while (iter.hasNext()) {
				 DrawPath drawPath = iter.next();
				 cacheCanvas.drawPath(drawPath.path, drawPath.paint);  
			 }
			 invalidate();// 刷新
		 }
		   /*在这里保存图片纯粹是为了方便,保存图片进行验证*/
	}
	
	/**清空画布*/
	public void clearScreen(){
		if(canvas != null){
			Paint backPaint = new Paint();
			backPaint.setColor(Color.WHITE);
			canvas.drawRect(new Rect(0, 0, width, height), backPaint);
			cacheCanvas.drawRect(new Rect(0, 0, width, height), backPaint);
		}
		invalidate();
	}
	
}
