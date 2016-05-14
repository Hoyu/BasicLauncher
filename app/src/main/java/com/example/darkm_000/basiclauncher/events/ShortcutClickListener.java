package com.example.darkm_000.basiclauncher.events;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.darkm_000.basiclauncher.Pack;

/**
 * Created by darkm_000 on 13/05/2015.
 */
public class ShortcutClickListener implements View.OnClickListener {

    Context context;

    public ShortcutClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        //We use .Tag() to obtain the info for the position in the array
        Intent data= (Intent) v.getTag();
        context.startActivity(data);

    }
}
