package com.example.user.test_camera.MainSource;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.test_camera.R;

/**
 * Created by user on 8/20/2015.
 */
public class AdapterType extends BaseAdapter
{
    private LayoutInflater inflater;
    private Context context;
    private Activity activity;
    private Bitmap icon;


    public AdapterType(Context context)
    {

        this.context = context;
        this.activity= (Activity) context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return ListManager.NameType.length;
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
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.listselection, null);
        holder.titleimage=(TextView) rowView.findViewById(R.id.name);
    //    holder.image=(ImageViewCustomize) rowView.findViewById(R.id.image);

        Typeface myNewFace = Typeface. createFromAsset(context.getAssets(), "fonts/rock.TTF");
        holder.titleimage.setTypeface(myNewFace);
        holder.titleimage.setText(ListManager.NameType[position]);
    /*    holder.titleimage.setText(listfiles[position].getName());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(listfiles[position].getAbsolutePath(), options);
        holder.image.setImageBitmap(bitmap);*/

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Toast.makeText(context, "You Clicked "+ position, Toast.LENGTH_LONG).show();

                switch (position){
                    case 0:
                        Intent intent1 = new Intent(context,QRCodeScanerActivity.class);
                         context.startActivity(intent1);
                        ((Activity)context).overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        break;
                    case 1:
                        Intent intent2 = new Intent(context,SelectionActivity.class);
                        context.startActivity(intent2);
                        ((Activity)context).overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        break;
                    case 2:
                        Intent intent3 = new Intent(context,SettingActivity.class);
                        context.startActivity(intent3);
                        break;
                }


            }
        });
        return rowView;
    }



}
