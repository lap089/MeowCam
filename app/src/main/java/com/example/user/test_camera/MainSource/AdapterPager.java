package com.example.user.test_camera.MainSource;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.test_camera.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by user on 8/23/2015.
 */
public class AdapterPager extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;
    ImageView image;
    Bitmap icon;
    private ArrayList<File> file;
    // constructor
    public AdapterPager(Context context, Activity activity,ArrayList<File> file) {
        this.context = context;
        this.file =file;
        icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.loading);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        icon = icon.createScaledBitmap(icon, width, height, true);
    }

    @Override
    public int getCount() {
        return file.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {


        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.imageslider, container,
                false);

        image = (ImageView) viewLayout.findViewById(R.id.image);
        Bitmap bitmap = BitmapFactory.decodeFile(file.get(position).getAbsolutePath());
        if(PreservedBitmap.Preserves.get(position)!=null) {
            bitmap = PreservedBitmap.Preserves.get(position);
            image.setImageBitmap(bitmap);
        }
        else
        {
            image.setImageBitmap(icon);
            new Loadimage(bitmap,image,position).execute();
        }



        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }



    private class Loadimage extends AsyncTask<byte[],Bitmap,Bitmap>
    {
        private ImageView imageView;
        private Bitmap bitmap;
        private int position;

        public Loadimage(Bitmap bitmap, ImageView imageView, int position)
        {
            this.imageView = imageView;
            this.bitmap = bitmap;
            this.position = position;
        }


        @Override
        protected Bitmap doInBackground(byte[]... params) {

         return   BitmapHelper.getScaleBitmapFromBitmap(bitmap);

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
            PreservedBitmap.Preserves.set(position,result);
        }
    }

}

