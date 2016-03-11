package com.example.user.test_camera.MainSource;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.test_camera.R;
import com.example.user.test_camera.tool.Mailer;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import uk.co.senab.photoview.PhotoViewAttacher;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.ToxicBakery.viewpager.transforms.BackgroundToForegroundTransformer;
import com.ToxicBakery.viewpager.transforms.CubeInTransformer;
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.ToxicBakery.viewpager.transforms.DepthPageTransformer;
import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.ToxicBakery.viewpager.transforms.FlipVerticalTransformer;
import com.ToxicBakery.viewpager.transforms.ForegroundToBackgroundTransformer;
import com.ToxicBakery.viewpager.transforms.RotateDownTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.ToxicBakery.viewpager.transforms.ScaleInOutTransformer;
import com.ToxicBakery.viewpager.transforms.StackTransformer;
import com.ToxicBakery.viewpager.transforms.TabletTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomInTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomOutTranformer;


public class ImageEditorActivity extends ActionBarActivity {

    private String LOG_TAG = ImageEditorActivity.class.getSimpleName();

    public int BLUR = 0;
    public int MINOR = 1;
    private CallbackManager callbackManager;
    private ImageView imageview;
  //  private Button orc;
  //  private ImageButton Minorbutton;
    private String path;
    private String imageName;
    private Bitmap bitmap;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private ImageButton shareButton;
    private ViewPager pager;
    private int CurrentFile;
    private ArrayList<File> file;
    //google drive
    GoogleApiClient mGoogleApiClient = null;
    GoogleApiClient.ConnectionCallbacks callbacks;
    GoogleApiClient.OnConnectionFailedListener connectionFailedListener;
    int GOOGLE_DRIVE_LOGIN_REQUEST_CODE = 101;
    String GOOGLEDRIVE_LOG_TAG = "GOOGLE DRIVE";
    int ACCOUNT_PICKER_REQUEST_CODE = 102;
    int AFTER_UPLOAD_REQUEST_CODE = 103;
    ImageButton uploadButton;
    //<

    //email
    ImageButton eMailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);

        imageview = (ImageView) findViewById(R.id.imageview);
        pager = (ViewPager) findViewById(R.id.pager);
        path = getIntent().getExtras().getString(getResources().getString(R.string.extra_raw_image_path), null);
        CurrentFile = getIntent().getExtras().getInt("CurrentFile",-1);

        Log.d(LOG_TAG,"path = "+path);
        Log.d(LOG_TAG,"CurrentFile = "+CurrentFile);

        uploadButton = (ImageButton) findViewById(R.id.upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleAccountPicker();
            }
        });

        eMailButton = (ImageButton) findViewById(R.id.send);
        eMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mailer send = new Mailer(ImageEditorActivity.this);
                File mFile = new File(path);
                send.SendMail(null,"MeowPic from MeowCam","MeowPic from MeowCam",mFile);
            }
        });

        shareButton = (ImageButton) findViewById(R.id.share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowShareDialog();
            }
        });


        //    Minorbutton = (ImageButton) findViewById(R.id.minor);
    //    orc = (Button) findViewById(R.id.orc);

       // imageName = getIntent().getExtras().getString(getResources().getString(R.string.extra_raw_image_name),null);
        File imgFile = new File(path);
        imageName = imgFile.getName();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mDrawerList = (ListView)findViewById(R.id.list_feature_nav);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        mDrawerList.setAdapter(new AdapterFeature(this,path,imageName));

        bitmap = BitmapHelper.getScaleBitMapFromFile(path);
        /*bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        if(bitmap!=null) {
            //     int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
            //     Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            //     bitmap = scaled;
            //      if(size.x < bitmap.getWidth() && size.y < bitmap.getHeight())
            //    {
          //  double scale = (double) size.x/bitmap.getWidth();
          //  Bitmap scaled = Bitmap.createScaledBitmap(bitmap,size.x/2 , size.y/2, true);
            bitmap = BitmapHelper.getScaleBitmapFromBitmap(bitmap);
            //  }
        }*/

        if(CurrentFile != -1) {
            imageview.setVisibility(View.GONE);
            pager.setVisibility(View.VISIBLE);
            file = (ArrayList<File>) getIntent().getExtras().getSerializable("ImageFiles");
            pager.setAdapter(new AdapterPager(this,ImageEditorActivity.this,file));
            pager.setCurrentItem(CurrentFile);
            pager.setPageTransformer(true, new CubeOutTransformer());
            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    Log.d("Check position change", "Pos: " + position);
                    path = file.get(position).getAbsolutePath();
                    mDrawerList.setAdapter(new AdapterFeature(ImageEditorActivity.this,path,file.get(position).getName()));
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
        else {
            imageview.setImageBitmap(bitmap);

        }


        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager!=null)
         callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_DRIVE_LOGIN_REQUEST_CODE)
            if (resultCode == RESULT_OK) {
                Log.i(GOOGLEDRIVE_LOG_TAG,"onConnection failed has resolution result");
                mGoogleApiClient.connect();
            }
        if (requestCode == ACCOUNT_PICKER_REQUEST_CODE)
        {
            if (data!=null) {
                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                Log.i(GOOGLEDRIVE_LOG_TAG, "result account = " + accountName);
                LoginGoogleApi(accountName);
            }
        }
        if (requestCode == AFTER_UPLOAD_REQUEST_CODE)
        {
            Log.i(GOOGLEDRIVE_LOG_TAG,"upload complete");
        }

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






    public void ShowShareDialog()
    {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Toast.makeText(ImageEditorActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                        ShowDialogContent();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }



    public void ShowDialogContent()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogshare);
        dialog.setTitle("Share");
        dialog.setCanceledOnTouchOutside(true);
        ImageButton share = (ImageButton) dialog.findViewById(R.id.share);
        final TextView title = (TextView) dialog.findViewById(R.id.title);
        final ImageView sharedimage = (ImageView) dialog.findViewById(R.id.sharedimage);
        final EditText comment = (EditText) dialog.findViewById(R.id.comment);
        File imgFile = new File(path);
        final Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        sharedimage.setImageBitmap(bitmap);
        title.setText(imgFile.getName().replace("jpg","").toUpperCase());

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareContent(bitmap,title.getText().toString(),comment.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.show();

    }




    public void ShareContent(Bitmap image, String ti, String com)
    {
        SharePhoto photo = new SharePhoto.Builder().setBitmap(image)
                .setCaption(ti.toUpperCase() + "  ----  " + com)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, null);
        Toast.makeText(this,"Shared!",Toast.LENGTH_SHORT).show();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_editor, menu);
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




    ////email ///////

    /////////google drive ///////


    public void LoginGoogleApi(String AccountName)
    {
        callbacks = new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.i(GOOGLEDRIVE_LOG_TAG, "onConnected");
                File mFile = new File(path);
                saveFiletoDrive(mFile);
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.i(GOOGLEDRIVE_LOG_TAG,"onConnectionSuspended");
            }
        };

        connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                if (connectionResult.hasResolution()) {
                    try {
                        //For first login when user choose account then ask for permission
                        //must call onActivityResult
                        Log.i(GOOGLEDRIVE_LOG_TAG,"onConnection failed has resolution");
                        connectionResult.startResolutionForResult(ImageEditorActivity.this, GOOGLE_DRIVE_LOGIN_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        // Unable to resolve, message user appropriately
                        Log.i(GOOGLEDRIVE_LOG_TAG, "something wrong");
                        e.printStackTrace();
                    }
                } else {
                    GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), ImageEditorActivity.this, 0).show();
                }
            }
        };
        Log.i(GOOGLEDRIVE_LOG_TAG,"set account name " + AccountName);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .setAccountName(AccountName)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(callbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
        //mGoogleApiClient.clearDefaultAccountAndReconnect();
        mGoogleApiClient.connect();
    }

    public void GoogleAccountPicker()
    {
        Log.i(GOOGLEDRIVE_LOG_TAG,"account picker");
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                false, null, null, null, null);
        startActivityForResult(intent, ACCOUNT_PICKER_REQUEST_CODE);
    }

    public void saveFiletoDrive(final File mFile){
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        String TAG = "Drive new content";
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Failed to create new contents.");
                            return;
                        }
                        // Otherwise, we can write our data to the new contents.
                        Log.i(TAG, "New contents created.");

                        InputStream is;

                        try{
                            is = new FileInputStream(mFile);
                            // Get an output stream for the contents.
                            OutputStream outputStream = result.getDriveContents().getOutputStream();
                            // Write inputstream to outputstream
                            org.apache.commons.io.IOUtils.copy(is, outputStream);
                        } catch (IOException e1) {
                            Log.i(TAG, "Unable to write file contents.");
                        }
                        // Create the initial metadata - MIME type and title.
                        // Note that the user will be able to change the title later.
                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("image/jpeg").setTitle(mFile.getName()).build();
                        // Create an intent for the file chooser, and start it.
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(mGoogleApiClient);
                        try {
                            startIntentSenderForResult(intentSender, AFTER_UPLOAD_REQUEST_CODE, null, 0, 0, 0);
                        }catch (Exception e){e.printStackTrace();}
                    }
                });
    }
}
