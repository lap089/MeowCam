package com.example.user.test_camera.MainSource;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.user.test_camera.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by user on 8/25/2015.
 */
public class OcrDownloader {
private ProgressDialog mProgressDialog;

	public Context context;
	public OcrDownloader(Context context){
		this.context = context;
	}

	public void Download()
	{
		new DownloadTask().execute(ListManager.OcrLinks);
	}

	public int getIndex(String name)
	{
		for(int i =0 ;i< ListManager.OcrName.length;++i)
			if(ListManager.OcrName[i].equals(name))
				return i;
		return 0;
	}

	public String[] getList() {
		ArrayList<String> list = new ArrayList<>();
		String[] paths = new String[]{OcrActivity.DATA_PATH, OcrActivity.DATA_PATH + "tessdata/"};
		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					return null;
				}
			}

		}

		// lang.traineddata file with the app (in assets folder)
		// You can get them at:
		// http://code.google.com/p/tesseract-ocr/downloads/list
		// This area needs work and optimization
		for(int i=0;i<ListManager.OcrLang.length;++i)
		if (!(new File(OcrActivity.DATA_PATH + "tessdata/" + ListManager.OcrNameFile[i])).exists()) {
			list.add(ListManager.OcrName[i]);
		}
		return ((String[]) list.toArray());
	}


    public class DownloadTask extends AsyncTask<String, Integer, String> {

		private PowerManager.WakeLock mWakeLock;


		@Override
		protected String doInBackground(String... sUrl) {
			if(Looper.getMainLooper().getThread()==Thread.currentThread())
				Log.d("Check: ","UI thread");

			else Log.d("Check: ", "Background thread");

			InputStream input = null;
			OutputStream output = null;
			HttpURLConnection connection = null;
			try {
				URL url = new URL(sUrl[0]);
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();

				// expect HTTP 200 OK, so we don't mistakenly save error report
				// instead of the file
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					return "Server returned HTTP " + connection.getResponseCode()
							+ " " + connection.getResponseMessage();
				}

				// this will be useful to display download percentage
				// might be -1: server did not report the length
				//int fileLength = connection.getContentLength();
				//List fileLength  = connection.getHeaderFields().get("content-Lenght");
				int fileLength = 5473919;
				// download the file
				input = connection.getInputStream();
				//output = new FileOutputStream("/sdcard/file_name.extension");
				output = new FileOutputStream(Environment
						.getExternalStorageDirectory().toString() + "/SimpleAndroidOCR/tessdata/" + ListManager.OcrNameFile[0]);
				byte data[] = new byte[4096];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					// allow canceling with back button
					if (isCancelled()) {
						input.close();
						return null;
					}
					total += count;
					Log.d("Check: ",String.valueOf(total));
					// publishing the progress....
					if (fileLength > 0) // only if total length is known
						publishProgress((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}
			} catch (Exception e) {
				return e.toString();
			} finally {
				try {
					if (output != null)
						output.close();
					if (input != null)
						input.close();
				} catch (IOException ignored) {
				}

				if (connection != null)
					connection.disconnect();
			}

            return null;
		}


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// instantiate it within the onCreate method90

			mProgressDialog = new ProgressDialog(context);
			mProgressDialog.setMessage("Downloading...");
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(true);
			mProgressDialog.show();

			// take CPU lock to prevent CPU from going off if the user
			// presses the power button during download
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					getClass().getName());
			mWakeLock.acquire();

		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			// if we get here, length is known, now set indeterminate to false
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);
			mProgressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			mProgressDialog.dismiss();
			mWakeLock.release();
			if(Looper.getMainLooper().getThread()==Thread.currentThread())
				Log.d("Check: ","UI thread");
			else Log.d("Check: ","Background thread");

			if (result != null)
				Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
			else {
				Toast.makeText(context, "Download done, start processing files!", Toast.LENGTH_SHORT).show();

			}

			}
		}

}

