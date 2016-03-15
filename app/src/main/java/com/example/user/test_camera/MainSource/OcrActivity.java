package com.example.user.test_camera.MainSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.test_camera.R;
import com.example.user.test_camera.tool.Mailer;
import com.example.user.test_camera.tool.SaveFile;
import com.googlecode.tesseract.android.TessBaseAPI;

public class OcrActivity extends AppCompatActivity {
    public static final String PACKAGE_NAME = "com.datumdroid.android.ocr.simple";
    public static String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/SimpleAndroidOCR/";

    // You should have the trained data file in assets folder
    // You can get them at:
    // http://code.google.com/p/tesseract-ocr/downloads/list
    public static String lang = "eng";

    private static final String TAG = "SimpleAndroidOCR.java";

    protected ImageView _image;
    protected EditText _field;
    protected String _path;
    protected boolean _taken;
    protected Bitmap bitmap;
    protected ProgressDialog mProgressDialog;
    protected static final String PHOTO_TAKEN = "photo_taken";
    private File ocr_result_file = null;
    File imgFile = null;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ImageView saveButton;
    private ImageView mailButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orc);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        ocr_result_file = null;

        saveButton = (ImageView) findViewById(R.id.saveButton_id);
        mailButton = (ImageView) findViewById(R.id.mailButton_id);
        _image = (ImageView) findViewById(R.id.orcimage);
        _field = (EditText) findViewById(R.id.recognizedtext);
        _path = getIntent().getExtras().getString(getResources().getString(R.string.extra_raw_image_path), null);


         imgFile = new File(_path);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);


        bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        if(bitmap!=null) {

            bitmap = BitmapHelper.getScaleBitmapFromBitmap(bitmap);

            _image.setImageBitmap(bitmap);
        }

        new Thread() {
            @Override
            public void run() {
                InitializeORC();
            }
        }.start();

        Log.d("Check ocr", "start phototaken");
        onPhotoTaken();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFile();
            }
        });
        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(OcrActivity.PHOTO_TAKEN, _taken);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState()");
        if (savedInstanceState.getBoolean(OcrActivity.PHOTO_TAKEN)) {
            onPhotoTaken();
        }
    }


    public void InitializeORC()
    {
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };
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
        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

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

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }

    }

    protected void onPhotoTaken() {

    new StartOcr().execute();

    }

    private void saveToFile(){
        String ocr_name =  preferences.getString(getResources().getString(R.string.pref_ocr_file_name), getResources().getString(R.string.pref_ocr_file_default_name));
        ocr_name = ocr_name+".txt";
        String ocr_result = _field.getText().toString();
        SaveFile sf = new SaveFile(ocr_name,ocr_result,this);
         ocr_result_file = sf.getFile();
        Toast.makeText(this,"saved to "+ocr_result_file.getPath(),Toast.LENGTH_SHORT).show();
    }

    private void sendMail(){
        if (ocr_result_file==null)
            saveToFile();
        Mailer mailer = new Mailer(this);
        String mail_body = "the ocr result of photo "+imgFile.getName()+" is "+"\n"+ _field.getText().toString();
        mailer.SendMail(null, "OCR of photo " + imgFile.getName(), mail_body, ocr_result_file);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_orc, menu);
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
            intent.putExtra(getResources().getString(R.string.extra_raw_image_path),_path);
            NavUtils.navigateUpTo(this, intent);
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }




    private class StartOcr extends AsyncTask<byte[],Bitmap,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            BitmapHelper.lockOrientation(OcrActivity.this);
            if(mProgressDialog != null)
                mProgressDialog = null;
            mProgressDialog = new ProgressDialog(OcrActivity.this,R.style.Toptobottom);
            mProgressDialog.setTitle("Analysing...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }


        @Override
        protected String doInBackground(byte[]... params) {

            TessBaseAPI baseApi = new TessBaseAPI();
            baseApi.setDebug(true);
            baseApi.init(DATA_PATH, lang);
            baseApi.setImage(bitmap);

            String recognizedText = baseApi.getUTF8Text();

            baseApi.end();

            // You now have the text in recognizedText var, you can do anything with it.
            // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
            // so that garbage doesn't make it to the display.

            Log.v(TAG, "OCRED TEXT: " + recognizedText);

            if ( lang.equalsIgnoreCase("eng") ) {
                recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
            }

            recognizedText = recognizedText.trim();
            return recognizedText;
        }

        @Override
        protected void onPostExecute(String recognizedText) {
            super.onPostExecute(recognizedText);
            mProgressDialog.dismiss();
            if ( recognizedText.length() != 0 ) {
                _field.setText(_field.getText().toString().length() == 0 ? recognizedText : _field.getText() + " " + recognizedText);
                _field.setSelection(0);
            }

            Log.d("Check ocr", "End phototaken");
        }
    }


}
