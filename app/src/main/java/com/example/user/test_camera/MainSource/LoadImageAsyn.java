package com.example.user.test_camera.MainSource;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.user.test_camera.R;

/**
 * Created by user on 8/26/2015.
 */

public class LoadImageAsyn extends AsyncTask<byte[],Bitmap,Bitmap>
{
    private SubsamplingScaleImageView imageView;
    private String path;
    private static LayoutInflater inflater=null;
    private Context context;
    private  Bitmap icon;
    public LoadImageAsyn(String path, SubsamplingScaleImageView imageView, Context context)
    {
        this.imageView = imageView;
        this.path = path;
        this.context = context;
        icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.loading);
     //   inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    protected Bitmap doInBackground(byte[]... params) {
        //       Log.d("Check pager:", "get bitmap scale " + position );
        Bitmap bitmap = BitmapHelper.getBitmapFromFile(path);
        return   BitmapHelper.getScaleBitmapFromBitmap(bitmap);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        imageView.setImage(ImageSource.bitmap(icon));
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        //       Log.d("Check pager:", "set bitmap result " + position);
       //View rowview = inflater.inflate(R.layout.activity_image_editor, null);

           // imageView = (SubsamplingScaleImageView) rowview.findViewById(R.id.imageview);
            imageView.setImage(ImageSource.bitmap(result));
    }
}
