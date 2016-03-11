package com.example.user.test_camera.MainSource;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


import com.example.user.test_camera.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;


public class SelectionActivity extends AppCompatActivity implements android.support.v7.app.ActionBar.TabListener {
    private String LOG_TAG = SelectionActivity.class.getSimpleName();
    private GridView gridimage;
    private ProgressDialog mProgressDialog;
    private File[] file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayShowTitleEnabled(true);
        gridimage = (GridView) findViewById(R.id.listimage);

        for (int i=0; i < 3; i++) {
            android.support.v7.app.ActionBar.Tab tab = bar.newTab();
            tab.setText(ListManager.NameDirs[i]);
            tab.setTabListener(this);
            bar.addTab(tab);
        }

        gridimage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SelectionActivity.this, ImageEditorActivity.class);
                intent.putExtra(getResources().getString(R.string.extra_raw_image_path), file[position].getAbsolutePath());
                intent.putExtra(getResources().getString(R.string.extra_raw_image_name), file[position].getName());
                ArrayList<File> files = new ArrayList<>(Arrays.asList(file));
                intent.putExtra("ImageFiles",files);
                intent.putExtra("CurrentFile",position);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }
        });







    }



    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selection, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        new LoadDataAsyn().execute(tab.getPosition());
    }



    @Override
    public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }



    private class LoadDataAsyn extends AsyncTask<Integer, Void, File[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            Log.i(LOG_TAG,"Load Data Asyn");
            if(mProgressDialog != null)
                mProgressDialog = null;
            mProgressDialog = new ProgressDialog(SelectionActivity.this,R.style.Toptobottom);
            // Set progressdialog title
            mProgressDialog.setTitle("Load Images");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected File[] doInBackground(Integer... value) {

            File folder = ListManager.Dirs.get(value[0]);
            //   File folder = new File(BitmapHelper.getAppFolder());
           file = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    String lowername = filename.toLowerCase();
                    if (lowername.endsWith(".jpg"))
                        return true;
                    return false;
                }
            });


            /*for(int i=0;i<file.length;++i)// FIXME: 8/21/2015 null pointer
                Log.d("Check directory",file[i].getAbsolutePath());*/

            PreservedBitmap.Preserves = new ArrayList<>();
            if(file == null)
                return null;
            for(int i=0; i< file.length;++i)
                PreservedBitmap.Preserves.add(null);
            return file;
        }

        @Override
        protected void onPostExecute(File[] result) {
            // Set the bitmap into ImageView
            //   imagemanga.setImageBitmap(result);
            // Close progressdialog
            if(mProgressDialog!=null)
                mProgressDialog.dismiss();
            gridimage.setAdapter(new AdapterSelection(SelectionActivity.this,result));
        }
    }

}
