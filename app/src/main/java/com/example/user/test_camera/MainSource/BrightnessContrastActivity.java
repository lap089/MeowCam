package com.example.user.test_camera.MainSource;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.user.test_camera.Effect.GLToolbox;
import com.example.user.test_camera.Effect.TextureRenderer;
import com.example.user.test_camera.R;

import java.io.File;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BrightnessContrastActivity extends Activity implements GLSurfaceView.Renderer {

    private String LOG_TAG = BrightnessContrastActivity.class.getSimpleName();

    private String rawPath;
    private File rawFile;


    private GLSurfaceView mEffectView = null;
    private int[] mTextures = new int[3];
    private EffectContext mEffectContext;
    private Effect mEffectContrast;
    private Effect mEffectBrightness;
    private TextureRenderer mTexRenderer = new TextureRenderer();
    private int mImageWidth;
    private int mImageHeight;
    private boolean mInitialized = false;

    //
    private boolean saveFrame = false;

    //seekbar and button
    private SeekBar contrastBar;//set contrast arg
    private SeekBar brightnessBar;//set bright arg
    private Button saveButton;//save current image


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brightness_contrast);


        /**
         * Initialize the renderer and tell it to only render when
         * explicity requested with the RENDERMODE_WHEN_DIRTY option
         */
        mEffectView = (GLSurfaceView) findViewById(R.id.mEffectView_id);
        mEffectView.setEGLContextClientVersion(2);
        mEffectView.setRenderer(this);
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        initSeekBarButton();
        getFromIntent();
    }



    private void getFromIntent(){
        Bundle data = getIntent().getExtras();
        rawPath = data.getString(getResources().getString(R.string.extra_raw_image_path));
        rawFile = new File(rawPath);
    }

    //init seekbar, add listener
    private void initSeekBarButton(){

        contrastBar = (SeekBar) findViewById(R.id.mContrastSeekBar_id);
        contrastBar.setProgress(100);
        contrastBar.setMax(200);
        contrastBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateEffect();
            }
        });

        brightnessBar = (SeekBar) findViewById(R.id.mBrightnessSeekBar_id);
        brightnessBar.setProgress(100);
        brightnessBar.setMax(200);
        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateEffect();
            }
        });

        saveButton = (Button) findViewById(R.id.saveCurrentFrameButton_id);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentFrame();
            }
        });
    }

    private void updateEffect(){
        int brightProgress = brightnessBar.getProgress();
        float start_num_bright = 0f;
        float num_bright = start_num_bright + (float)brightProgress*0.01f ;
        int contrast_progress = contrastBar.getProgress();
        float start_num_contrast = 0f;
        float num_contrast = start_num_contrast + (float)contrast_progress*0.01f ;
        initEffect(num_contrast, num_bright);
        mEffectView.requestRender();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEffectView.requestRender();
    }

    private void loadTextures() {
        // Generate textures
        GLES20.glGenTextures(2, mTextures, 0);

        // Load input bitmap
        Bitmap bitmap = BitmapHelper.getScaleBitMapFromFile(rawPath);
        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();
        mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);

        // Upload to texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // Set texture parameters
        GLToolbox.initTexParams();
    }

    //will create effect with Arg from seekbar
    private void initEffect(float contrastArg, float brightnessArg) {
        EffectFactory effectFactory = mEffectContext.getFactory();
        if (mEffectBrightness != null) {
            mEffectBrightness.release();
        }
        if (mEffectContrast != null) {
            mEffectContrast.release();
        }


            mEffectBrightness = effectFactory.createEffect(
                    EffectFactory.EFFECT_BRIGHTNESS);
        mEffectBrightness.setParameter("brightness", brightnessArg);
            Log.i("Effect", "brightness = " + brightnessArg);



            mEffectContrast = effectFactory.createEffect(
                    EffectFactory.EFFECT_CONTRAST);
        mEffectContrast.setParameter("contrast", contrastArg);
            Log.i("Effect", "contrast = " + contrastArg);

    }

    private void applyEffect() {
        mEffectContrast.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);//apply contrast
        mEffectBrightness.apply(mTextures[1], mImageWidth, mImageHeight, mTextures[1]);//apply brightness

        Log.i("Effect", "apply");

    }


    //show on mEffectView
    private void renderResult() {
        mTexRenderer.renderTexture(mTextures[1]);
    }



    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mInitialized) {
            //Only need to do this once
            mEffectContext = EffectContext.createWithCurrentGlContext();
            mTexRenderer.init();
            loadTextures();
            mInitialized = true;
            initEffect(1.0f,1.0f);
        }


        // initEffect();
        applyEffect();

        renderResult();
        Log.i("Effect", "draw");

        if (saveFrame){
           // when call saveCurrentFrame(), saveFrame = true and it save current frame to bitmap and store
            Bitmap bmp = takeScreenshot(gl);


           String path = rawPath;//overwrite old image

            BitmapHelper.saveBitmap(bmp,path);
            saveFrame = false;

            Log.i(LOG_TAG, "save current frame at "+path);

            //go back to image editor with new image (overwrite old image)
            BitmapHelper.goToImageEditor(this,path);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mTexRenderer != null) {
            mTexRenderer.updateViewSize(width, height);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        mEffectView.requestRender();
        return true;
    }

    //source http://grishma102.blogspot.com/2013/10/apply-effects-on-image-using-effects.html
    private Bitmap takeScreenshot(GL10 mGL) {
        final int mWidth = mEffectView.getWidth();
        final int mHeight = mEffectView.getHeight();
        IntBuffer ib = IntBuffer.allocate(mWidth * mHeight);
        IntBuffer ibt = IntBuffer.allocate(mWidth * mHeight);
        mGL.glReadPixels(0, 0, mWidth, mHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);

        // Convert upside down mirror-reversed image to right-side up normal
        // image.
        for (int i = 0; i < mHeight; i++) {
            for (int j = 0; j < mWidth; j++) {
                ibt.put((mHeight - i - 1) * mWidth + j, ib.get(i * mWidth + j));
            }
        }

        Bitmap mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mBitmap.copyPixelsFromBuffer(ibt);
        return mBitmap;
    }

    //call to save picture
    public void saveCurrentFrame(){
        saveFrame = true;
        mEffectView.requestRender();
    }
}
