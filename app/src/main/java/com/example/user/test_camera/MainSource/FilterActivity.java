package com.example.user.test_camera.MainSource;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.test_camera.Effect.GLToolbox;
import com.example.user.test_camera.Effect.TextureRenderer;
import com.example.user.test_camera.R;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FilterActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String LOG_TAG = FilterActivity.class.getSimpleName();

    private String rawPath;
    private File rawFile;


    private GLSurfaceView mEffectView = null;
    private int[] mTextures = new int[3];
    private EffectContext mEffectContext;
    private Effect mEffect;

    private TextureRenderer mTexRenderer = new TextureRenderer();
    private int mImageWidth;
    private int mImageHeight;
    private boolean mInitialized = false;

    private int currentEffect = 0;


    //
    private boolean saveFrame = false;

    //seekbar and button
    private Button saveButton;//save current image

    //swipe
    private GestureDetectorCompat mDetector;
    private GestureDetector.OnGestureListener gestureListener;

    private String []listOfFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        setTitle("None");
        listOfFilter = getResources().getStringArray(R.array.filter);
        String []strings = listOfFilter;
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(strings));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.item,arrayList);
        ListView lv = (ListView) findViewById(R.id.left_drawer);
        lv.setAdapter(adapter);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //in manifest, add  android:theme="@android:style/Theme.Holo.Light.DarkActionBar" in applicant tab action bar work

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(LOG_TAG,"On item click listener "+position);
               setFilter(position);
                mDrawerLayout.closeDrawers();
            }
        });
        /**
         * Initialize the renderer and tell it to only render when
         * explicity requested with the RENDERMODE_WHEN_DIRTY option
         */
        mEffectView = (GLSurfaceView) findViewById(R.id.mEffectView_id);
        mEffectView.setEGLContextClientVersion(2);
        mEffectView.setRenderer(this);
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        initButton();
        getFromIntent();

        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initGestureDetector();//swipe left right change filter

    }

    private void initGestureDetector(){
        gestureListener = new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (velocityX > 0){
                    Log.i(LOG_TAG,"right");
                    increaseFilter();
                   }
                if (velocityX < 0){
                    Log.i(LOG_TAG,"left");
                    decreaseFilter();
                   }
                return true;
            }
        };
        mDetector = new GestureDetectorCompat(this,gestureListener);
    }

    private void getFromIntent(){
        Bundle data = getIntent().getExtras();
        rawPath = data.getString(getResources().getString(R.string.extra_raw_image_path));
        rawFile = new File(rawPath);
    }

    //init  button
    private void initButton(){
        saveButton = (Button) findViewById(R.id.saveCurrentFrameButton_id);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentFrame();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEffectView.requestRender();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.i(LOG_TAG,"dispathch touch event meow");
        mDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(LOG_TAG,"onTouchEvent mewo");
        mDetector.onTouchEvent(event);
        return true;
        //return super.onTouchEvent(event);
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

    //set effect
    private void setFilter(int filterNum){
        currentEffect = filterNum;
        initEffect();
        mEffectView.requestRender();
        Log.i(LOG_TAG, "set filter " + currentEffect);
        setTitle(listOfFilter[filterNum]);
        getSupportActionBar().setTitle(listOfFilter[filterNum]);
    }

    //swipe right
    private void increaseFilter(){
        if (currentEffect == 8) {
            setFilter(0);
        }
        else setFilter(currentEffect+1);
        Log.i(LOG_TAG,"increase filter ");
    }

    //swipe left
    private void decreaseFilter(){
        if (currentEffect == 0) {
            setFilter(8);
        }
        else setFilter(currentEffect-1);
        Log.i(LOG_TAG,"decrease filter ");
    }

    //will create effect with Arg from seekbar
    private void initEffect() {
        EffectFactory effectFactory = mEffectContext.getFactory();
        if (mEffect != null) {
            mEffect.release();
            mEffect = null;
        }
        if (currentEffect == 1) mEffect = effectFactory.createEffect(EffectFactory.EFFECT_CROSSPROCESS);
        if (currentEffect == 2) mEffect = effectFactory.createEffect(EffectFactory.EFFECT_DOCUMENTARY);
        if (currentEffect == 3) mEffect = effectFactory.createEffect(EffectFactory.EFFECT_GRAYSCALE);
        if (currentEffect == 4) mEffect = effectFactory.createEffect(EffectFactory.EFFECT_LOMOISH);
        if (currentEffect == 5) mEffect = effectFactory.createEffect(EffectFactory.EFFECT_NEGATIVE);
        if (currentEffect == 6) mEffect = effectFactory.createEffect(EffectFactory.EFFECT_POSTERIZE);
        if (currentEffect == 7) mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SEPIA);
        if (currentEffect == 8) mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SHARPEN);

    }

    private void applyEffect() {
        if (currentEffect!= 0)
        mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);//apply
        Log.i("Effect", "apply");

    }


    //show on mEffectView
    private void renderResult() {
        if (currentEffect!=0)
        mTexRenderer.renderTexture(mTextures[1]);
        else mTexRenderer.renderTexture(mTextures[0]);
    }



    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mInitialized) {
            //Only need to do this once
            mEffectContext = EffectContext.createWithCurrentGlContext();
            mTexRenderer.init();
            loadTextures();
            mInitialized = true;
            initEffect();
        }


        // initEffect();
        applyEffect();

        renderResult();
        Log.i("Effect", "draw");

        if (saveFrame){
            // when call saveCurrentFrame(), saveFrame = true and it save current frame to bitmap and store
            Bitmap bmp = takeScreenshot(gl);

            int counting = 0;
            String path = rawPath; //Will overwrite source file

            BitmapHelper.saveBitmap(bmp,path);
            saveFrame = false;
            Log.i(LOG_TAG, "save current frame at "+path);

            //after save file (overwrite), open imageEditor
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

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open, R.string.close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Utilities");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
               // getSupportActionBar().setTitle("Editor");
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
        getMenuInflater().inflate(R.menu.menu_filter, menu);
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
