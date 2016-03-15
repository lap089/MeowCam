package com.example.user.test_camera.MainSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.test_camera.R;

public class SettingActivity extends Activity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private EditText picNameEditText;
    private EditText ocrFileNameText;
    String fileName;
    String ocrName;
    Button saveButton;
    Button down;
    private int index = 0;
    public static Spinner listlang;
    public static  String[] ExistList;
    public static ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        picNameEditText = (EditText) findViewById(R.id.picName_id);
        ocrFileNameText = (EditText) findViewById(R.id.ocrName_id);

        saveButton = (Button) findViewById(R.id.saveButton_id);
        down = (Button) findViewById(R.id.down);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        BitmapHelper.updatePicName(this);
        fileName = BitmapHelper.PIC_NAME;
        ocrName = preferences.getString(getResources().getString(R.string.pref_ocr_file_name),getResources().getString(R.string.pref_ocr_file_default_name));

        picNameEditText.setText(fileName);
        ocrFileNameText.setText(ocrName);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName = picNameEditText.getText().toString();
                editor.remove(getResources().getString(R.string.pref_pic_name));
                editor.commit();
                editor.putString(getResources().getString(R.string.pref_pic_name), fileName);
                editor.commit();
                BitmapHelper.updatePicName(SettingActivity.this);
                editor.remove(getResources().getString(R.string.pref_ocr_file_name));
                editor.commit();
                editor.putString(getResources().getString(R.string.pref_ocr_file_name),ocrFileNameText.getText().toString());
                editor.commit();
                OcrActivity.lang = ListManager.OcrLang[index];
                editor.putInt(getResources().getString(R.string.pref_ocr_index), index);
                editor.apply();
                finish();
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDownloadDialog();
            }
        });

        listlang = (Spinner) findViewById(R.id.spiner);
       ExistList = new OcrDownloader(this).getExistList();
        adapter =  new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,ExistList );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listlang.setAdapter(adapter);

        index = preferences.getInt(getResources().getString(R.string.pref_ocr_index),0);
        if(index >= ExistList.length)
            index = 0;
        listlang.setSelection(index);
        OcrActivity.lang = ListManager.OcrLang[new OcrDownloader(this).getIndex(ExistList[index])];

        listlang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            protected Adapter initializedAdapter = null;

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (initializedAdapter != parentView.getAdapter()) {
                    initializedAdapter = parentView.getAdapter();
                    return;
                }
             //    ((TextView) parentView.getChildAt(0)).setTextColor(Color.BLUE);
                // ((TextView) parentView.getChildAt(0)).setTextSize(5);
                index = (int) listlang.getSelectedItemId();
                Log.d("Check spinner","choose: " + index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

    }


    public void ShowDownloadDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] NotExistList = new OcrDownloader(this).getNotExistList();
        builder.setTitle("Choose list: ");
        builder.setItems(NotExistList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                new OcrDownloader(SettingActivity.this).Download(NotExistList[item]);
                Toast.makeText(SettingActivity.this, "Start Download...", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
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
}
