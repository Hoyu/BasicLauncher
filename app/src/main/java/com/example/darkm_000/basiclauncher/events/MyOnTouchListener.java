package com.example.darkm_000.basiclauncher.events;

import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by darkm_000 on 13/05/2015.
 */
public class MyOnTouchListener implements View.OnTouchListener {

    //Margins for prevent the icons get off the screen
    int leftMargin;
    int topMargin;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(v.getWidth(), v.getHeight());
                //Here we get the event RAWx and y and adjust the result to put the icon in the center
                leftMargin=(int)event.getRawX() - v.getWidth()/2;
                topMargin=(int)event.getRawY() - v.getHeight()/2;
                //Take care of margins
                //Right side of screen
                if (leftMargin+v.getWidth() > v.getRootView().getWidth())
                    leftMargin=v.getRootView().getWidth() - v.getWidth();
                //Left side
                if (leftMargin < 0)
                    leftMargin=0;
                //bottom of screen
                if (topMargin + v.getHeight() > ((View)v.getParent()).getHeight())
                    topMargin=((View)v.getParent()).getHeight() - v.getHeight();
                //top of screen
                if (topMargin < 0)
                    topMargin=0;

                layoutParams.leftMargin= leftMargin;
                layoutParams.topMargin=topMargin;
                //Set the view with the new layout parameters
                v.setLayoutParams(layoutParams);

                break;
            case MotionEvent.ACTION_UP:
               // v.setOnTouchListener(null);
                break;
        }
        //to continue handling the event after this event
        return false;
    }
}
