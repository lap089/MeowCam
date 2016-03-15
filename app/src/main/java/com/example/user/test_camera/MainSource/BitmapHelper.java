package com.example.user.test_camera.MainSource;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Surface;

import com.example.user.test_camera.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Thien on 8/15/2015.
 */
public class BitmapHelper {
    public static String APP_NAME = "MeowCamera";
    public static String PIC_NAME = "MeowPic";
    public static String EXTENSION = ".jpg";
    private static String LOG_TAG = BitmapHelper.class.getSimpleName();

    public static void updatePicName(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        PIC_NAME=preferences.getString(context.getResources().getString(R.string.pref_pic_name),context.getResources().getString(R.string.pref_picname_default_name));
    }


    public static Bitmap getBitmapFromFile(String rawPath){
        try{

            File rawFile = new File(rawPath);
            InputStream is = new FileInputStream(rawFile);

            Bitmap bitmap = BitmapFactory.decodeStream(is);

            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
            Log.e(LOG_TAG,"getBitmapFromFile error");
        }
        return null;
    }

    public static Bitmap getScaleBitMapFromFile(String rawPath){
        try{

            File rawFile = new File(rawPath);
            InputStream is = new FileInputStream(rawFile);

            Bitmap bitmap = BitmapFactory.decodeStream(is);
            bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/2,bitmap.getHeight()/2,false);
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
            Log.e(LOG_TAG,"getScaleBitMapFromFile error");
        }
        return null;
    }

    public static Bitmap getScaleBitmapFromBitmap(Bitmap bitmap)
    {
        double width = MainActivity.size.x;

        if(width < bitmap.getWidth())
        {

            double scalar = (double) bitmap.getWidth()/width;
            double dwidth = ((double)bitmap.getWidth())/scalar;
            double dheight = bitmap.getHeight()/scalar;
            bitmap = Bitmap.createScaledBitmap(bitmap,(int) (dwidth),(int)dheight , true);
        }
        return bitmap;
    }


    public static void saveBitmap(Bitmap bitmap, String path){
        try{
        File file = new File(path);
        FileOutputStream fOut = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        fOut.flush();
        fOut.close();}
        catch (IOException e){
            e.printStackTrace();
            Log.e(LOG_TAG,"saveBitmaptoDownload error");
        }
    }

    //get app folder where will store image
    public static String getAppFolder(){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/";
        return path;
    }

    //get new directory for new image
    public static String getNewImagePath(){
        String base = getAppFolder();
        String path = base + PIC_NAME+EXTENSION;
        File file = new File(path);
        int counting = 0;
        while (file.exists()){
            counting++;
            path = base+PIC_NAME+counting+EXTENSION;
            file = new File(path);
        }
        return path;
    }

    //pass an inmage and go to ImageEditor with that image
    public static void goToImageEditor(Context context ,String path) {
        Intent intent = new Intent(context, ImageEditorActivity.class);
        intent.putExtra(context.getResources().getString(R.string.extra_raw_image_path), path);
        //  context.startActivity(intent);
        Activity activity = (Activity) context;
        NavUtils.navigateUpTo(activity, intent);
    }


    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void lockOrientation(Activity context) {
        int rotation = context.getWindowManager().getDefaultDisplay().getRotation();

        switch(rotation) {
            case Surface.ROTATION_180:
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case Surface.ROTATION_270:
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case  Surface.ROTATION_0:
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Surface.ROTATION_90:
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }


    public static void setPicName(String newFileName){
        PIC_NAME = newFileName;
    }

    public static String getPicName(){
        return PIC_NAME;
    }
}
