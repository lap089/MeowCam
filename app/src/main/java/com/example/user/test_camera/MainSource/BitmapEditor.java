package com.example.user.test_camera.MainSource;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

/**
 * Created by Thai Thien on 6/26/2015.
 * Meow Meow
 */
public class BitmapEditor {
    /*Keep Calm And Meow On*/

    /*
    * Black and white bitmap
    * input: source bitmap
    * output: black and white bitmap
    * */
    public static Bitmap Color2Gray(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
       // Canvas canvas = new Canvas(result);// what is it ?
        int c;
        int c1;
        for (int x=0; x<w; x++)
            for (int y=0;y<h;y++)
            {
                int temp;
                c = bmp.getPixel(x,y);

                //USE THIS RATIO
                temp = (int) (Color.red(c)*0.3 +Color.green(c)*0.58 + Color.blue(c)*0.11);
                c1= Color.argb(255,temp,temp,temp);
                result.setPixel(x,y,c1);
            }
        return result;
    }

    /* just not working
    * Blur effect to bitmap
    * input: source bitmap
    * output: blur bitmap
    * */
   public static Bitmap Blur1(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);// what is it ?
        canvas.drawBitmap(bmp,0,0,null);// copy bitmap



        int center_pixel;
        int top;
        int bot;
        int left;
        int right;

        int c;
        int c1;
        for (int x=1; x<w-1; x=x+1)
            for (int y=1;y<h-1;y=y+1)
            {
                int temp;
                center_pixel = bmp.getPixel(x,y);
                top = bmp.getPixel(x,y+1);
                bot = bmp.getPixel(x,y-1);
                left = bmp.getPixel(x-1,y);
                right = bmp.getPixel(x+1,y);

                int red = (Color.red(top)+Color.red(bot)+Color.red(left)+Color.red(right)+Color.red(center_pixel))/5;
                int blue = (Color.blue(top)+Color.blue(bot)+Color.blue(left)+Color.blue(right)+Color.blue(center_pixel))/5;
                int green = (Color.green(top)+Color.green(bot)+Color.green(left)+Color.green(right)+Color.green(center_pixel))/5;
                center_pixel = Color.argb(255,red,green,blue);

                result.setPixel(x,y,center_pixel);
            }
        return  result;
    }


    public static Bitmap BoxBlur(Bitmap bmp, int box_range)
    {


        int w = bmp.getWidth();
        int h = bmp.getHeight();

        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);// what is it ?
      //  canvas.drawBitmap(bmp,0,0,null);// copy bitmap

        //int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
       // bmp.getPixels(pixels, 0, w, 0, 0, w, h);



        for (int i=0;i<w-box_range+1;i++)
            for (int j = 0;j<h-box_range+1;j++)
            {

                //a box loop
                int avg_r  = 0;//average (we sum all pix and divide for num of pix)
                int avg_b  = 0; // blue
                int avg_g  = 0; //green
                int num_of_pix = box_range*box_range;//number of pixel in box
                for (int a = 0;a<box_range;a++)
                    for (int b = 0; b<box_range;b++)
                    {
                        int tmp_pix = bmp.getPixel(i+a,j+b);
                        //int tmp_pix = pixels[(j+b)*w+i+a];
                        avg_r = avg_r+  Color.red(tmp_pix);
                        avg_b = avg_b + Color.blue(tmp_pix);
                        avg_g = avg_g + Color.green(tmp_pix);
                    }

                avg_r = avg_r/num_of_pix;
                avg_b = avg_b/num_of_pix;
                avg_g = avg_g/num_of_pix;
                //end box

                //we set avg to center pixel
                int center_x = i+box_range/2;
                int center_y = j+box_range/2;
                int center_pix = Color.argb(255,avg_r,avg_g,avg_b);
                result.setPixel(center_x,center_y,center_pix);
            }

        return result;
    }

    public static Bitmap BoxBlurImprove(Bitmap bmp, int box_range,int Scale_range)
    {

        int w = bmp.getWidth();
        int h = bmp.getHeight();
        w = w/Scale_range;
        h= h/Scale_range;
        bmp = Bitmap.createScaledBitmap(bmp, w, h, false);

        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        int num_of_pix=box_range*box_range;


        for (int i=0;i<w-box_range+1;i++) {
        //init box
            int sum_r=0;
            int sum_b=0;
            int sum_g=0;
            for (int a = 0;a<box_range;a++)
                for (int b = 0; b<box_range;b++)
                {
                    int tmp_pix = bmp.getPixel(i+a,b);
                    sum_r = sum_r+  Color.red(tmp_pix);
                    sum_b = sum_b + Color.blue(tmp_pix);
                    sum_g = sum_g + Color.green(tmp_pix);
                }

            for (int j = 0; j < h - box_range + 1; j++) {
                //each time it move down, minus top row, add botton row
                if (j>0)
                    for (int a = 0;a<box_range;a++){
                        int top_pix = bmp.getPixel(i+a,j-1);//pix we have pass
                        int bot_pix = bmp.getPixel(i+a,j+box_range-1);//new pix in box
                        sum_r = sum_r - Color.red(top_pix) + Color.red(bot_pix);
                        sum_b = sum_b - Color.blue(top_pix) + Color.blue(bot_pix);
                        sum_g = sum_g - Color.green(top_pix) + Color.green(bot_pix);
                    }


                //set center pixel
                int avg_r = sum_r/num_of_pix;
                int avg_b = sum_b/num_of_pix;
                int avg_g = sum_g/num_of_pix;

                int central_pix = Color.argb(255,avg_r,avg_g,avg_b);
                result.setPixel(i+box_range/2,j+box_range/2,central_pix);
            }
        }
        result = Bitmap.createScaledBitmap(result,w*Scale_range,h*Scale_range,false);
        return result;
    }

    public static Bitmap BlurScaled(Bitmap bmp, int Scale_range){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        w = w/Scale_range;
        h= h/Scale_range;

        Bitmap result = Bitmap.createScaledBitmap(bmp,w,h,false);
        result = Bitmap.createScaledBitmap(result,w*Scale_range,h*Scale_range,false);
        return result;
    }

    public static Bitmap MirrorLeftToRight(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        for (int x=0; x < w/2; x++)
            for (int y=0;y<h;y++)
            {
                int tmp_pix = bmp.getPixel(w/2-x,y);
                result.setPixel(w/2-x,y,tmp_pix);
                result.setPixel(w/2+x,y,tmp_pix);
            }
        return result;
    }


    public static Bitmap MirrorRightToLeft(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        for (int x=0; x < w/2; x++)
            for (int y=0;y<h;y++)
            {
                int tmp_pix = bmp.getPixel(w/2+x,y);
                result.setPixel(w/2+x,y,tmp_pix);
                result.setPixel(w/2-x,y,tmp_pix);
            }
        return result;
    }

    public static Bitmap flipBitmap(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        for (int x=0; x < w; x++)
            for (int y=0;y<h;y++)
            {
                int tmp_pix = bmp.getPixel(x,y);
                result.setPixel(w-x-1,y,tmp_pix);
            }
        return result;
    }

}


