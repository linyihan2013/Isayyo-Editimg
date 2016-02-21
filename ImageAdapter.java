package com.isayyo.app.editimg;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.isayyo.app.R;

public class ImageAdapter extends BaseAdapter{
    private Context context = null;
    // references to our images
    private Integer[] mThumbIds = {R.drawable.b1, R.drawable.b2, R.drawable.b3, R.drawable.b4,
    		R.drawable.b5, R.drawable.b6, R.drawable.b7, R.drawable.b8, 
    		R.drawable.b9, R.drawable.b10, R.drawable.b11, R.drawable.b12};
    //步骤1： 构造函数       
    public ImageAdapter(Context context){
        this.context = context;
    }
    //步骤2：BaseAdapter需要重构四个方法getCount(),getItem(),getItemId(int position),getView()
    //步骤2.1：getCount() 表示How many items are in the data set represented by this Adapter.
    public int getCount() {
        return mThumbIds.length;
    }
    //步骤2.2：getItem() 根据需要position获得布放在GridView的对象。在这个例子中，我们不需要处理里面的对象，可以设为null
    public Object getItem(int position) {
        return null;
    }
    
    //步骤2.3：getItemId() 获得row id（Get the row id associated with the specified position in the list），由于我们也不需要，简单的设为0
    public long getItemId(int position) {
        return 0;
    }
    //步骤2.4：获得GridView里面的View，Get a View that displays the data at the specified position in the data set. 和第一个例子一样，传递的第二个函数可能为null，必须进行处理。
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;
        if(convertView == null){
            imageView = new ImageView(context);
            // 设置View的height和width：这样保证无论image原来的尺寸，每个图像将重新适合这个指定的尺寸。
            imageView.setLayoutParams(new GridView.LayoutParams(85,85));
            /* ImageView.ScaleType.CENTER 但不执行缩放比例
             * ImageView.ScaleType.CENTER_CROP 按比例统一缩放图片（保持图片的尺寸比例）便于图片的两维（宽度和高度）等于或大于相应的视图维度
             * ImageView.ScaleType.CENTER_INSIDE 按比例统一缩放图片（保持图片的尺寸比例）便于图片的两维（宽度和高度）等于或小于相应的视图维度 */
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8,8,8,8);
        }else{
            imageView = (ImageView)convertView;
        }
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }
    
}