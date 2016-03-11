package com.example.user.test_camera.MainSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.user.test_camera.R;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;


public class QRCodeScanerActivity extends AppCompatActivity {

    private Boolean FlashMode = false;
    private String TAG = "Zbar";
    private String LIFECYCLE_TAG="LifeCycle";
    private FrameLayout frameLayout;// a placeholder for code scanner preview
    private ZBarScannerView myScannerView;// scanner view which we will add into frameLayout
    private ZBarScannerView.ResultHandler resultHandler;//result handler for myScannerView

    Button FlashOnOffButton;

    private Menu mOptionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_activity);

        frameLayout = (FrameLayout) findViewById(R.id.PlaceHolderFram);

        //TODO: init the result handler
        resultHandler = new ZBarScannerView.ResultHandler() {
            @Override
            public void handleResult(Result result) {
                // Do something with the result here
                Log.v(TAG, result.getContents()); // Prints scan results
                Log.v(TAG, result.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)
                turnOnCamera();//Keep the light on

                //Result show here
                ShowResultDialog(result.getContents());
            }
        };

        //TODO: init view and add view to layout
        myScannerView = new ZBarScannerView(QRCodeScanerActivity.this);
        frameLayout.addView(myScannerView);

        //A button to turn flash on and off

    }

    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }


    @Override
    protected void onResume() {
        super.onResume();
        turnOnCamera();
    }

    private void turnOnCamera(){
        //TODO: SET Result Handle Here
        myScannerView.setResultHandler(resultHandler);
        //TODO Start camera
        myScannerView.startCamera();
        //TODO Set flash and autofocus
        myScannerView.setFlash(FlashMode);
        myScannerView.setAutoFocus(true);
    }

    protected void onStop() {
        // Stop camera on stop
        super.onStop();
        Log.i(LIFECYCLE_TAG, "onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LIFECYCLE_TAG, "onDestroy");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(LIFECYCLE_TAG, "onPause");
        myScannerView.stopCamera();
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
            TurnFlashOnOff();
        }

        return super.onOptionsItemSelected(item);
    }

    private void ShowResultDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeScanerActivity.this);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // MainActivity.this.onResume();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void TurnFlashOnOff(){
        if (FlashMode) {
            FlashMode = false;
            mOptionMenu.findItem(R.id.action_flash).setIcon(getResources().getDrawable(R.drawable.flash_off));
        }
        else {
            FlashMode = true;
            mOptionMenu.findItem(R.id.action_flash).setIcon(getResources().getDrawable(R.drawable.flash_on));
        }
        myScannerView.setFlash(FlashMode);
    }

}
