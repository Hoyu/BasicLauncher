package com.example.darkm_000.basiclauncher;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.darkm_000.basiclauncher.events.AppClickListener;
import com.example.darkm_000.basiclauncher.events.MyOnTouchListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by darkm_000 on 14/05/2015.
 */
public class Pack implements Serializable{

    private static final int INDEX_CHILD = 0;
    //To serialize we can't use the Drawable. We create coordinates x, y and a String with the icon path directory
    transient Drawable icon;
    String name;
    String packageName;
    String label;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    int x, y;
    String iconPath;

    public String getPackageName() {
        return packageName;
    }
    public String getName() {
            return name;
        }

    //We use this methods to save the location of the icon in the app

    public void cache(){
        if (iconPath==null){
            //Create directory for saving cached icons of the apps
            new File(MainActivity.activity.getApplicationInfo().dataDir+"/CachedApps/").mkdirs();
        }
        if (icon != null){
            iconPath=MainActivity.activity.getApplicationInfo().dataDir+"/CachedApps/"+packageName+name;
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(iconPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (fileOutputStream!=null){
                //We compress the icon image (quality 0-100 but for png it's ignored)
                drawableToBitmap(icon).compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else
                iconPath=null; //Prevents repetition of failure
        }
    }

    public void deleteCached(){
        if(iconPath!=null)
            new File(iconPath).delete();
    }

    public Bitmap getCached(){

        //We get a bitmap of the icon stored
        //We use BitmapFactory to get the bitmap from the file cached
        //BitmapFactory.Options will help us configure the decoded bitmap
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=false; //If set to true, the decoder will return null (no bitmap), but the out...
        options.inPreferredConfig= Bitmap.Config.ARGB_8888; //If this is non-null, the decoder will try to decode into this internal configuration.
        options.inDither=true; //If dither is true, the decoder will attempt to dither the decoded image.

        if (iconPath!=null){
            File iconCached=new File(iconPath);
            if (iconCached.exists())
                //If we really have an icon and a file: we do decodeFile with our options
                return BitmapFactory.decodeFile(iconCached.getAbsolutePath(), options);
            else
                return null;
        }else
            return null;

    }

    public static Bitmap drawableToBitmap(Drawable drawable){
        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable) drawable).getBitmap();
        //If it isn't drawable we have to create the bitmap (Config.ARGB_8888)
        Bitmap bitmap=Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        //1.We use Canvas to draw the bitmap
        Canvas canvas=new Canvas(bitmap);
        //2.Set the bounds
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        //3.And simply draw it
        drawable.draw(canvas);

        return bitmap;
    }


    public void addToHome(final Context context, final RelativeLayout home){
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //the coordinates are float
        layoutParams.leftMargin=x;
        layoutParams.topMargin=y;

        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        //With the inflater we get the linear layout
        final LinearLayout linearLayout=(LinearLayout) inflater.inflate(R.layout.drawer_item, null);
        //We insert the view we recieve with the event click to the new layout

        if(icon==null){
            icon=new BitmapDrawable(context.getResources(), getCached());
        }
        ((ImageView)linearLayout.findViewById(R.id.icon_image)).setImageDrawable(icon);
        ((TextView)linearLayout.findViewById(R.id.icon_text)).setText(label);

        //Add the OnTouch event for Drag and Drop
        linearLayout.setOnTouchListener(new MyOnTouchListener());
        //In order to avoid apps for launching when drag and drop using longclick
       /* linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                /*
                //ImageView Setup
                ImageView imageView = new ImageView(context);
                //setting image resource
                imageView.setBackgroundColor(context.getResources().getColor(R.color.error_color));
                //setting image position
                imageView.setLayoutParams(new RelativeLayout.LayoutParams(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,context.getResources().getDisplayMetrics()),
                        RelativeLayout.LayoutParams.MATCH_PARENT));

                //adding view to layout
                home.addView(imageView);
                */

                /*linearLayout.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {

                        switch (event.getAction()){
                            case DragEvent.ACTION_DROP:

                                if((v.getX() >= home.getX()+30) && (v.getY() >= home.getY() +30) )
                                    linearLayout.removeView(v);
                                break;
                        }


                       return true;
                    }
                });*//*
                //home.removeView(imageView);
                return false;
            }
        });*/
        linearLayout.setOnClickListener(new AppClickListener(context));

        linearLayout.setTag(this);
        //We put it into our home view in the same coordinates with the layourParams
        home.addView(linearLayout, INDEX_CHILD, layoutParams);

    }

}
