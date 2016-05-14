package com.example.darkm_000.basiclauncher.events;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.AdapterView;

import com.example.darkm_000.basiclauncher.MainActivity;
import com.example.darkm_000.basiclauncher.Pack;

/**
 * Created by darkm_000 on 11/05/2015.
 */
public class GridClickListener implements AdapterView.OnItemClickListener {

    Context context;
    PackageManager packageManager;
    Pack[] packs;

    public GridClickListener(Context context, PackageManager packageManager, Pack[] packs) {
        this.context = context;
        this.packageManager = packageManager;
        this.packs = packs;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent launchIntent=new Intent(Intent.ACTION_MAIN);
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        //Identifier for a specific application component
        ComponentName componentName=new ComponentName(packs[position].getPackageName(), packs[position].getName());

        launchIntent.setComponent(componentName);

        context.startActivity(launchIntent);
    }
}
