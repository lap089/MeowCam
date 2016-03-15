package com.example.user.test_camera.MainSource;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.enrique.stackblur.StackBlurManager;
import com.example.user.test_camera.R;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

//source of module StackBlur
// https://github.com/kikoso/android-stackblur
public class BlurActivity extends AppCompatActivity {


    //use lib
    StackBlurManager stackBlurManager;

    //view
    ImageView imageView;
    SeekBar seekBar;
    Button saveButton;
    private PhotoViewAttacher mphotoViewAttacher;
    //raw image
    File rawFile = null;
    String rawPath = null;
    Bitmap rawBitmap = null; //
    Bitmap scaleBitmap = null;
    //result image
    Bitmap resultBitmap = null;

    Point size ;

    private int SAVE_HIGH_QUALITY_MODE = 0;
    private int PREVIEW_LOW_QUALITY_MODE = 1;

    //async
    //souce https://github.com/kikoso/android-stackblur/blob/master/StackBlurDemo/src/com/example/stackblurdemo/BenchmarkActivity.java
    private BenchmarkTask benchmarkTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);


        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        //get intent
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        rawPath = data.getString(getResources().getString(R.string.extra_raw_image_path));
        rawFile = new File(rawPath);
        rawBitmap = BitmapHelper.getBitmapFromFile(rawPath);
        scaleBitmap = BitmapHelper.getScaleBitMapFromFile(rawPath);

        //view
        imageView = (ImageView) findViewById(R.id.blurImageview_id);
        seekBar = (SeekBar) findViewById(R.id.blurSeekbar_id);
        saveButton = (Button) findViewById(R.id.saveResultbt_id);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveResultBitMap();
            }
        });

        Bitmap bitmap = scaleBitmap;
        imageView.setImageBitmap(bitmap);
        mphotoViewAttacher = new PhotoViewAttacher(imageView);

      

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // stackBlurManager.process(progress);
                //imageView.setImageBitmap(stackBlurManager.returnBlurredImage());
                Log.i("progress", "p = " + progress);

               
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (benchmarkTask != null) {
                    benchmarkTask.cancel(true);
                }
                benchmarkTask = new BenchmarkTask();
                benchmarkTask.execute(seekBar.getProgress(),PREVIEW_LOW_QUALITY_MODE);
            }
        });
    }

//    protected Bitmap getInputImage()
//    {
//        try{
//
//            rawFile = new File(rawPath);
//            InputStream is = new FileInputStream(rawFile);
//
//            Bitmap bitmap = BitmapFactory.decodeStream(is);
//
//            return bitmap;
//        }catch (Exception e){e.printStackTrace();}
//        return null;
//    }

    //@param[0] : blurAmount
    //@param[1]: save mode (0 = raw high quality for save, 1 = low quality for preview)
    private class BenchmarkTask extends AsyncTask<Integer, Void, Bitmap> {
        private int max = Integer.MIN_VALUE;
        private Bitmap outBitmap;
        private int mode;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            mode = params[1];
            int blurAmount = params[0];
            Bitmap inBitmap = null, blurredBitmap = null;
            Paint paint = new Paint();

            // TODO: 8/15/2015 scale bitmap
            if (mode == PREVIEW_LOW_QUALITY_MODE) {
                inBitmap = scaleBitmap;// low quality for preview
            }

            if (mode == SAVE_HIGH_QUALITY_MODE){
                inBitmap = rawBitmap; //high quality for save
            }

            outBitmap = Bitmap.createBitmap(inBitmap.getWidth(), inBitmap.getHeight(), inBitmap.getConfig());
            Canvas canvas = new Canvas(outBitmap);

            StackBlurManager blurManager = new StackBlurManager(inBitmap);


            // Java
            blurredBitmap = blurManager.process(blurAmount);
            canvas.save();
            canvas.clipRect(0, 0, outBitmap.getWidth(), outBitmap.getHeight());
            canvas.drawBitmap(blurredBitmap, 0, 0, paint);
            canvas.restore();
            publishProgress();
            blurredBitmap.recycle();

            if(isCancelled())
                return outBitmap;

            return outBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (mode == PREVIEW_LOW_QUALITY_MODE) {
                //display for preview only
                imageView.setImageBitmap(result);
                resultBitmap = result;
            }
             if (mode ==SAVE_HIGH_QUALITY_MODE){
                //save to SD card
                    int counting = 0;
                    String savePath = rawPath;//overwrite old path
                BitmapHelper.saveBitmap(result,savePath);
                //Toast.makeText(BlurActivity.this,"saved to "+savePath,Toast.LENGTH_SHORT).show();
                 BitmapHelper.goToImageEditor(BlurActivity.this,savePath);
            }
        }


    }

    private void saveResultBitMap(){
        //TODO: save resultBitmap to sd card
        new BenchmarkTask().execute(seekBar.getProgress(),SAVE_HIGH_QUALITY_MODE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blur, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, ImageEditorActivity.class);
            intent.putExtra(getResources().getString(R.string.extra_raw_image_path),rawPath);
            NavUtils.navigateUpTo(BlurActivity.this, intent);
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
