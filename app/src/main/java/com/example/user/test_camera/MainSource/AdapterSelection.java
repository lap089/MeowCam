package com.example.user.test_camera.MainSource;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.test_camera.R;

import java.io.File;


public class AdapterSelection extends BaseAdapter
{
    private LayoutInflater inflater;
    private Context context;
    private Activity activity;
    private Bitmap icon;
    private File[] listfiles;
    private Point size;

    public AdapterSelection(Context context,File[] listfiles)
    {
        this.listfiles = listfiles;
        this.context = context;
        this.activity= (Activity) context;
        inflater = LayoutInflater.from(context);
        Point size;
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        this.size = size;
        icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.loading);
    }


    @Override
    public int getCount() {
        if(listfiles != null)
        return listfiles.length;
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    public class Holder
    {
        TextView titleimage;
        ImageViewCustomize image;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder;
        View rowView;


          //  Log.d("Check listview", " " + position);
            rowView = inflater.inflate(R.layout.listselection, null);
            holder = new Holder();
            holder.titleimage = (TextView) rowView.findViewById(R.id.name);
            holder.image = (ImageViewCustomize) rowView.findViewById(R.id.image);

        holder.image.setImageBitmap(icon);
    //    Typeface myNewFace = Typeface. createFromAsset(context.getAssets(), "fonts/rock.TTF");
     //   holder.titleimage.setTypeface(myNewFace);
        holder.titleimage.setText(listfiles[position].getName().replace(".jpg", ""));
        if(PreservedBitmap.Preserves.get(position) == null)
            new AsyncTask<Holder, Void, Bitmap>() {
                private Holder v;

                @Override
                protected Bitmap doInBackground(Holder... params) {
                    v = params[0];
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(listfiles[position].getAbsolutePath(), options);


                    double width = size.x;
                    if(width < bitmap.getWidth())
                    {
                        double scalar = (double) bitmap.getWidth()/width;
                        double dwidth = ((double)bitmap.getWidth())/scalar;
                        double dheight = bitmap.getHeight()/scalar;
                        Bitmap scaled = Bitmap.createScaledBitmap(bitmap,(int) (dwidth),(int)dheight , true);
                        bitmap = scaled;
                    }
                 /*   else if(width > bitmap.getWidth())
                    {
                        double scalar =  width/bitmap.getWidth();
                        double dwidth = ((double)bitmap.getWidth())*scalar - 30;
                        double dheight = bitmap.getHeight()*scalar - 30;
                        Bitmap scaled = Bitmap.createScaledBitmap(bitmap,(int) (dwidth),(int)dheight , true);
                        bitmap = scaled;

                    }*/

                    PreservedBitmap.Preserves.add(position,bitmap);
                    return bitmap;
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    result = PreservedBitmap.Preserves.get(position);
                    super.onPostExecute(result);
                    v.image.setImageBitmap(result);
                //    Log.d("Check imageadapter", "set image");
                //    Log.d("Check outofmemory", " " + result.getByteCount());

                }
            }.execute(holder);

        else {
        //    Log.d("Check imageadapter","set preserve");
            holder.image.setImageBitmap(PreservedBitmap.Preserves.get(position));
        }


        return rowView;
    }



}

