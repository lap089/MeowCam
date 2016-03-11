package com.example.user.test_camera.MainSource;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.test_camera.R;

/**
 * Created by user on 8/20/2015.
 */
public class AdapterFeature extends BaseAdapter
{
    private LayoutInflater inflater;
    private Context context;
    private Activity activity;
    private Bitmap icon;
    private String path;
    private String name;
    public AdapterFeature(Context context,String path,String name)
    {
        this.name = name;
        this.path = path;
        this.context = context;
        this.activity= (Activity) context;
        //icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.camera1);
        inflater = LayoutInflater.from(context);

        //get list of icon of feature
        ListManager.imageFeatureID = context.getResources().obtainTypedArray(R.array.feature_icon);

    }


    @Override
    public int getCount() {
        return ListManager.NameFeature.length;
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
        TextView name;
        ImageView image;
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.listfeature, null);
        holder.name=(TextView) rowView.findViewById(R.id.name);
        holder.image=(ImageView) rowView.findViewById(R.id.image);

        Typeface myNewFace = Typeface. createFromAsset(context.getAssets(), "fonts/rock.TTF");
        holder.name.setTypeface(myNewFace);


        holder.image.setImageDrawable(ListManager.imageFeatureID.getDrawable(position));
        holder.name.setText(ListManager.NameFeature[position]);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                    Intent intent = new Intent(context, ListManager.ClassFeature.get(position));

                    intent.putExtra(context.getResources().getString(R.string.extra_raw_image_path), path);
                    intent.putExtra(context.getResources().getString(R.string.extra_raw_image_name), name);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

            }
        });
        return rowView;


    }





}
