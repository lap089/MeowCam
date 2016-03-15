package com.example.user.test_camera.MainSource;

/**
 * Created by user on 6/10/2015.
 */



import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class Preview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "Preview";
    private Float mDist=0.0f;//distance pinch

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the pointer ID
        Camera.Parameters params = camera.getParameters();
        int action = event.getAction();


        if (event.getPointerCount() > 1) {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                camera.cancelAutoFocus();
                handleZoom(event, params);
            }
        }
        return true;
    }

    //source http://stackoverflow.com/questions/8120753/android-camera-preview-zoom-using-double-finger-touch/10578099#10578099
    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        camera.setParameters(params);
    }



    /** Determine the space between the first two fingers */
    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        Float fl = new Float(x * x + y * y);
        return (float)Math.sqrt(fl.doubleValue());
    }


    public static SurfaceHolder mHolder;
    public Context context;
    public static Camera camera;
    public static int rotation;
    Preview(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        this.context = context;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        //camera = Camera.open();
        Log.i(TAG, "surfaceCreated");
        try {
            if(camera == null)
               return;

/*
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {

                camera.setDisplayOrientation(90);

            } else {

                camera.setDisplayOrientation(90);
            }*/


          //  setCameraDisplayOrientation((Activity)context,MainActivity.currentId,camera);



            camera.setPreviewDisplay(holder);

            camera.setPreviewCallback(new PreviewCallback() {

                public void onPreviewFrame(byte[] data, Camera arg1) {
             /*       FileOutputStream outStream = null;
                    try {
                        outStream = new FileOutputStream(String.format(
                                "/sdcard/Download/temp.jpg", System.currentTimeMillis()));
                        outStream.write(data);
                        outStream.close();
                     //   Log.d(TAG, "onPreviewFrame - wrote bytes: "
                      //          + data.length);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                    }
              */      Preview.this.invalidate();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  /*  public void PauseCamera()
    {
        if(camera!=null) {
            camera.release();
            camera = null;
        }
    }*/

  @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        if(camera != null) {
          camera.setPreviewCallback(null);
          camera.stopPreview();
          mHolder.removeCallback(this);
          camera.release();
          camera = null;
          MainActivity.camera = null;
      }
      //camera.stopPreview();


      Log.i(TAG,"SurfaceDestroy");
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        Camera.Parameters parameters = camera.getParameters();
      //  parameters.setFlashMode(parameters.FLASH_MODE_ON);
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

        Log.i(TAG,"SurfaceChange");
        // You need to choose the most appropriate previewSize for your app
        Camera.Size previewSize = previewSizes.get(0);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        //auto focus
        //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        //parameters.setFocusMode(parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

       /* if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            parameters.set("orientation", "portrait");
            parameters.set("rotation",90);
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            parameters.set("orientation", "landscape");
            parameters.set("rotation", 0);
        }
*/
        rotation= setCameraDisplayOrientation((Activity)context,MainActivity.currentId,camera);
      //  parameters.set("rotation",result);
        camera.setParameters(parameters);
        camera.startPreview();

        Camera.Parameters focusParameters = camera.getParameters();


        if (focusParameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_AUTO)) {
            focusParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        camera.setParameters(focusParameters);

        Log.i(TAG, "Start preview in SurfaceChange");
    }


//N6KXJ-P6YWY-4C92Q-J7BVB-R6XGM

    public static int setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
        return result;
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint p = new Paint(Color.RED);
        Log.d(TAG, "draw");
        canvas.drawText("PREVIEW dsaasdasa", canvas.getWidth() / 2,
                canvas.getHeight() / 2, p);
    }
}
