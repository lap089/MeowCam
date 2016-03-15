package com.example.user.test_camera.MainSource;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.user.test_camera.R;

public class MirrorActivity extends AppCompatActivity {

    private ImageView minorimage;
    private String path;
    private Button save;
    private Bitmap bitmap = null;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minor);
        minorimage = (ImageView) findViewById(R.id.minor_image);
        save = (Button) findViewById(R.id.saveCurrentFrameButton_id);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapHelper.saveBitmap(bitmap, path);
                BitmapHelper.goToImageEditor(MirrorActivity.this, path);
            }
        });

        path = getIntent().getExtras().getString(getResources().getString(R.string.extra_raw_image_path));
        bitmap = BitmapHelper.getBitmapFromFile(path);
        minorimage.setImageBitmap(bitmap);

        new MinorFeature().execute();
    }


    private class MinorFeature extends AsyncTask<Integer, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            BitmapHelper.lockOrientation(MirrorActivity.this);
            mProgressDialog = new ProgressDialog(MirrorActivity.this,R.style.Toptobottom);
            mProgressDialog.setMessage("Processing...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            try {

                bitmap = BitmapEditor.MirrorLeftToRight(bitmap);
                //     File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"bmp1.png");
                //     OutputStream os1 = new FileOutputStream(file);
                //     bitmap.compress(Bitmap.CompressFormat.JPEG,100,os1);
                Log.d("Check minor","Done");
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }

          return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            minorimage.setImageBitmap(result);
            //  SendMail("test1");
            //  Toast.makeText(ImageEditorActivity.this,"Done!")
            mProgressDialog.dismiss();
            minorimage.setImageBitmap(result);
        }

    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_minor, menu);
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
                intent.putExtra(getResources().getString(R.string.extra_raw_image_path),path);
                NavUtils.navigateUpTo(this, intent);
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;

            }


            return super.onOptionsItemSelected(item);
        }
    }
