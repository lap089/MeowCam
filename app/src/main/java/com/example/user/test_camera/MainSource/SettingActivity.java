package com.example.user.test_camera.MainSource;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.test_camera.R;

public class SettingActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    EditText picNameEditText;
    String fileName;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        picNameEditText = (EditText) findViewById(R.id.picName_id);
        saveButton = (Button) findViewById(R.id.saveButton_id);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        BitmapHelper.updatePicName(this);
        fileName = BitmapHelper.PIC_NAME;
        picNameEditText.setText(fileName);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName = picNameEditText.getText().toString();
                editor.remove(getResources().getString(R.string.pref_pic_name));
                editor.commit();
                editor.putString(getResources().getString(R.string.pref_pic_name), fileName);
                editor.commit();
                BitmapHelper.updatePicName(SettingActivity.this);
            }
        });
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
