package com.example.user.test_camera.MainSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.test_camera.R;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    public static Point size;
    Preview preview;
    ImageButton buttonClick;

   public static Camera camera;
    public static int currentcamera;
private String path;
private String imageName;
public ImageView myImage;
    public static int currentId;
    public ImageButton exchange;


    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    //flash mode
    int flash_mode = 0;
    int FLASH_AUTO = 1;
    int FLASH_OFF = 0;
    int FLASH_ALWAYS =2;

    private Menu mOptionMenu;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BitmapHelper.updatePicName(this);//update name from pref
        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        if(receivedAction.equals(Intent.ACTION_SEND)){
            Uri receivedUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            Intent intent = new Intent(MainActivity.this, ImageEditorActivity.class);
            intent.putExtra(getResources().getString(R.string.extra_raw_image_path), getRealPathFromURI(receivedUri));
            startActivity(intent);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            finish();
            Log.d("Check action",getRealPathFromURI(receivedUri));
        }

        else {

            currentcamera = Camera.CameraInfo.CAMERA_FACING_BACK;

            camera = getCameraInstance(currentcamera);
            exchange = (ImageButton) findViewById(R.id.exchange);
            preview = new Preview(this, camera);
            ((FrameLayout) findViewById(R.id.preview)).addView(preview);
            myImage = (ImageView) findViewById(R.id.imageview);
            buttonClick = (ImageButton) findViewById(R.id.buttonClick);


            Display display = getWindowManager().getDefaultDisplay();
            size = new Point();
            display.getSize(size);
            Log.d(TAG, "Screen size: " + size.x + "-" + size.y);

            mDrawerList = (ListView) findViewById(R.id.list_type_nav);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mActivityTitle = getTitle().toString();
            mDrawerList.setAdapter(new AdapterType(this));


            new Thread() {
                @Override
                public void run() {
                    InitializeORC();
                }
            }.start();


            buttonClick.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    new Thread() {
                        @Override
                        public void run() {

                            Camera.Parameters focusParameters = camera.getParameters();
                            if (focusParameters.getSupportedFocusModes().contains(
                                    Camera.Parameters.FOCUS_MODE_AUTO)) {
                                //will focus camera and take photo
                                preview.camera.autoFocus(new Camera.AutoFocusCallback() {
                                    @Override
                                    public void onAutoFocus(boolean success, Camera camera) {
                                        preview.camera.takePicture(shutterCallback, rawCallback,
                                                jpegCallback);
                                    }
                                });
                            } else {
                                //take photo without focus
                                preview.camera.takePicture(shutterCallback, rawCallback,
                                        jpegCallback);
                            }
                        }
                    }.start();

                }
            });


            myImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    Intent intent = new Intent(MainActivity.this, ImageEditorActivity.class);
                    intent.putExtra(getResources().getString(R.string.extra_raw_image_path), path);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                }
            });


            exchange.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    //      finish();
                    //        Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    //        currentcamera = currentcamera == Camera.CameraInfo.CAMERA_FACING_BACK ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
                    //        intent.putExtra("Camerafacing",currentcamera);
                    //    startActivity(intent);

                    ((FrameLayout) findViewById(R.id.preview)).removeView(preview);
                    currentcamera = currentcamera == Camera.CameraInfo.CAMERA_FACING_BACK ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
                    camera = getCameraInstance(currentcamera);
                    preview = new Preview(MainActivity.this, camera);
                    ((FrameLayout) findViewById(R.id.preview)).addView(preview);
                }
            });


            Log.i(TAG, "onCreate'd");


            setupDrawer();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }
    }

    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onShutter'd");
        }
    };

    /** Handles data for raw pictcure */
    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken - raw");
        }
    };

    /** Handles data for jpeg picture */
    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            Log.d(TAG, "Saving file..." + data.length);

                  createFile createfile = new createFile();
                    createfile.execute(data);


        }

    };




    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open, R.string.close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Feature");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mOptionMenu = menu;
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

        if (id == R.id.action_flash){
            changeFlashMode();
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void InitializeORC()
    {
        String[] paths = new String[] { OcrActivity.DATA_PATH, OcrActivity.DATA_PATH + "tessdata/" };
        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }

        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization
        if (!(new File(OcrActivity.DATA_PATH + "tessdata/" + OcrActivity.lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + OcrActivity.lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(OcrActivity.DATA_PATH
                        + "tessdata/" + OcrActivity.lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len,count =0 ;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                    count += len;
                    Log.d(TAG, "Copied " + count);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v(TAG, "Copied " + OcrActivity.lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + OcrActivity.lang + " traineddata " + e.toString());
            }
        }

    }



    private class createFile extends AsyncTask<byte[],Bitmap,Bitmap>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, values[0].getWidth() + "-" + values[0].getHeight());
        //    int nh = (int) ( values[0].getHeight() * (512.0 / values[0].getWidth()) );
         //   Bitmap scaled = Bitmap.createScaledBitmap(values[0], 512, nh, true);
          /*  if(size.x < values[0].getWidth() && size.y < values[0].getHeight())
            {

                Bitmap scaled = Bitmap.createScaledBitmap(values[0],size.x/4, size.y/4, true);
                values[0] = scaled;
            }*/
            values[0] = BitmapHelper.getScaleBitmapFromBitmap(values[0]);
            myImage.setImageBitmap(values[0]);
        }

        @Override
        protected Bitmap doInBackground(byte[]... params) {

            FileOutputStream outStream = null;
            try {
                // write to local sandbox file system
                // outStream =
                // CameraDemo.this.openFileOutput(String.format("%d.jpg",
                // System.currentTimeMillis()), 0);
                // Or write to sdcard
               // imageName = String.format("%d_lap", System.currentTimeMillis());
              //  path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/"+ imageName+".jpg";
                path = BitmapHelper.getNewImagePath();
             //   outStream = new FileOutputStream(path);
             //   outStream.write(params[0]);
             //   outStream.close();

                try {
             //       File f = new File(path);
                  /*  ExifInterface exif = new ExifInterface(f.getPath());
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    int angle = 0;

                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                        angle = 90;
                    }
                    else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                        angle = 180;
                    }
                    else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                        angle = 270;
                    }*/

                    camera.startPreview();
                    Matrix mat = new Matrix();
                    int rotation = Preview.rotation;
                    if (currentId == 1)//fix front cam upside down
                           rotation = rotation + 180;

                    mat.postRotate(rotation);
                    Log.i(TAG, "rotation = " + rotation);
                    Log.i(TAG,"rotation cam id = "+currentId);
                /// /    Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f), null, null);
                    Bitmap bmp = BitmapFactory.decodeByteArray(params[0],0,params[0].length);
                    Bitmap correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
                    if (currentId==1){
                        correctBmp = BitmapEditor.flipBitmap(correctBmp);
                    }

                    publishProgress(correctBmp);
                    // bmp.recycle();

                    // TODO: 8/15/2015 replace it with bitmap helper

                    BitmapHelper.saveBitmap(correctBmp,path);
                   /* File file = new File(path);
                    FileOutputStream fOut = new FileOutputStream(file);
                    correctBmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                    fOut.flush();
                    fOut.close();*/


                    Log.i(TAG, "onPictureTaken - wrote bytes: " + params[0].length);
                    Log.i(TAG, "path = "+path);
                    return correctBmp;
                }
                catch(OutOfMemoryError oom) {
                    Log.w("TAG", "-- OOM Error in setting image");
                }






                //  SetImage();

         //   } catch (FileNotFoundException e) {
         //       e.printStackTrace();
         //   } catch (IOException e) {
         //       e.printStackTrace();
            } finally {
            }
         return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            Toast.makeText(MainActivity.this,"Done!!!",Toast.LENGTH_SHORT);
        }
    }



    @Override
    public void onResume()
    {
        super.onResume();

        if(camera == null) {
            camera = getCameraInstance(currentcamera);
            preview = new Preview(this,camera);
            ((FrameLayout) findViewById(R.id.preview)).addView(preview);

            Log.i(TAG, "onResume");

        }
    }



   

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        Log.d("Preview","on pause");
        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
       /* if (camera != null) {
            //TODO:
            camera.release();
            camera = null;
            Log.i(TAG, "Release camera");
        }
*/
        ((FrameLayout) findViewById(R.id.preview)).removeView(preview);
    }



    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(int camera_facing){
        Camera c = null;
        try {
                int numberOfCameras = Camera.getNumberOfCameras();
                for (int i = 0; i < numberOfCameras; i++) {
                    Camera.CameraInfo info = new Camera.CameraInfo();
                    Camera.getCameraInfo(i, info);
                    if (info.facing == camera_facing) {
                        //Log.d(DEBUG_TAG, "Camera found");
                        currentId = i;
                        break;
                    }
                }

            c = Camera.open(currentId); // attempt to get a Camera instance
            Log.i(TAG,"open camera");
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.e("CAMERA","No Camera");
        }

        return c; // returns null if camera is unavailable
    }

    private void setFlash_mode(int newFlashMode){
        //TODO: set flash mode
        Camera.Parameters parameters = Preview.camera.getParameters();
        flash_mode = newFlashMode;
        if (flash_mode == FLASH_AUTO){
            parameters.setFlashMode(parameters.FLASH_MODE_AUTO);
            mOptionMenu.findItem(R.id.action_flash).setIcon(getResources().getDrawable(R.drawable.flash_auto));
        }

        if (flash_mode == FLASH_ALWAYS){
            parameters.setFlashMode(parameters.FLASH_MODE_ON);
            mOptionMenu.findItem(R.id.action_flash).setIcon(getResources().getDrawable(R.drawable.flash_on));
        }

        if (flash_mode == FLASH_OFF){
            parameters.setFlashMode(parameters.FLASH_MODE_OFF);
            mOptionMenu.findItem(R.id.action_flash).setIcon(getResources().getDrawable(R.drawable.flash_off));
        }
        Preview.camera.setParameters(parameters);
    }

    private void changeFlashMode(){
        if (flash_mode==2)
            setFlash_mode(0);
        else setFlash_mode(flash_mode+1);
    }

}