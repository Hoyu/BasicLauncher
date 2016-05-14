package com.example.darkm_000.basiclauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by darkm_000 on 09/05/2015.
 */
public class DrawerAdapter extends BaseAdapter {

    //Best performance for any View scrolling smooth
    //Class for drawing a multicomposed view object declared in the drawer_item.xml
    static class ViewHolder {
        TextView text;
       // TextView timestamp;
        ImageView icon;
       // ProgressBar progress;
       // int position;
    }

    Context context;
    Pack packs[];

    public DrawerAdapter(Context context, Pack[] packs) {
        this.context = context;
        this.packs = packs;
    }

    @Override
    public int getCount() {
        return packs.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
        ImageView imageView=new ImageView(context);
        imageView.setImageDrawable(packs[position].icon);
        imageView.setLayoutParams(new GridView.LayoutParams(65, 65));
        imageView.setPadding(3,3,3,3);
        return imageView;
        */

        ViewHolder holder;
        //To work with our drawer_item.xml layout we need this inflater
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        //Recycling the View
        if(convertView==null){
            //Inflate with our xml layout and no root
            convertView=inflater.inflate(R.layout.drawer_item, null);
            //Now we asign to the ViewHolder the elements of the layout
            holder = new ViewHolder();
            holder.icon=(ImageView)convertView.findViewById(R.id.icon_image);
            holder.text = (TextView) convertView.findViewById(R.id.icon_text);
            //Performance purpouse
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        holder.icon.setImageDrawable(packs[position].icon);
        holder.text.setText(packs[position].label);

        return convertView;
    }
}
