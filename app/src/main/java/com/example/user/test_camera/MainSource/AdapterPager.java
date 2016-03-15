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

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
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
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public class Holder
    {
        SubsamplingScaleImageView image;
        Bitmap bitmap;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.loading);            // it is recycled

        Holder holder;
        holder = new Holder();
        View viewLayout = inflater.inflate(R.layout.imageslider, container,
                false);

        holder.image = (SubsamplingScaleImageView) viewLayout.findViewById(R.id.image);

   //     Log.d("Check pager:", "check bitmap preserved " + position );
        if(PreservedBitmap.Preserves.get(position)!=null) {
            holder.bitmap = PreservedBitmap.Preserves.get(position);
   //         Log.d("Check pager:", "set bitmap preserved " + position );
            holder.image.setImage(ImageSource.bitmap(holder.bitmap));
        }
        else
        {
            holder.image.setImage(ImageSource.bitmap(icon));
     //       Log.d("Check pager:", "Load image async " + position);
            new Loadimage(file.get(position).getAbsolutePath(),holder.image,position).execute();
        }



        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }



    public class Loadimage extends AsyncTask<byte[],Bitmap,Bitmap>
    {
        private SubsamplingScaleImageView imageView;
        private String path;
        private int position;

        public Loadimage(String path, SubsamplingScaleImageView imageView, int position)
        {
            this.imageView = imageView;
            this.path = path;
            this.position = position;
        }


        @Override
        protected Bitmap doInBackground(byte[]... params) {
     //       Log.d("Check pager:", "get bitmap scale " + position );
            Bitmap bitmap = BitmapHelper.getBitmapFromFile(path);
            return   BitmapHelper.getScaleBitmapFromBitmap(bitmap);

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            imageView.setImage(ImageSource.bitmap(result));
            PreservedBitmap.Preserves.set(position,result);
        }
    }

}

