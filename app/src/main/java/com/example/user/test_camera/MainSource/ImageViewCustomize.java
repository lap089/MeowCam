package com.example.user.test_camera.MainSource;

/**
 * Created by user on 8/6/2015.
 */import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewCustomize extends ImageView
{
    public ImageViewCustomize(Context context)
    {
        super(context);
    }

    public ImageViewCustomize(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ImageViewCustomize(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}