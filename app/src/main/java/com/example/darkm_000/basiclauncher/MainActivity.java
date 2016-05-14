package com.example.darkm_000.basiclauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.support.annotation.DimenRes;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.example.darkm_000.basiclauncher.events.GridClickListener;
import com.example.darkm_000.basiclauncher.events.GridLongClickListener;
import com.example.darkm_000.basiclauncher.events.MyOnTouchListener;
import com.example.darkm_000.basiclauncher.events.ShortcutClickListener;
import com.example.darkm_000.basiclauncher.serialize.SerializableData;
import com.example.darkm_000.basiclauncher.serialize.Serialization;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnTouchListener, View.OnDragListener {


    private static final int REQUEST_PASSWORD = 1;
    private static final int REQUEST_PICK_SHORTCUT = 2;
    private static final int REQUEST_CREATE_SHORTCUT = 3;
    //Grid with all the apps
    GridView grid;
    //Main screen/ user desktop
    RelativeLayout home;
    //the slide screen with the grid
    SlidingDrawer drawer;
    //We need an adapter to draw our screens
    DrawerAdapter drawerAdapter;

    Button botonCambiarFondo;

    View.OnTouchListener mInterceptTouchListener;

    public void setOnInterceptTouchListener(View.OnTouchListener listener) {
        mInterceptTouchListener = listener;
    }

    //With the packageManager we'll get the info of the system to our array of apps
    Pack[] packs;
    PackageManager packageManager;


    AppWidgetManager mAppWidgetManager;
    LauncherAppWidgetHost mAppWidgetHost;
    //To get access to our activity info (SAVING CHANGES IN ACTIVITY)
    static Activity activity;

    public static Activity getActivity() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new LauncherAppWidgetHost(this, R.id.APPWIDGET_HOST_ID);

        //we save our main activity in activity (SAVING CHANGES IN ACTIVITY)
        activity = this;

        packageManager = getPackageManager();
        //We get the GridView "content" that we created in the xml
        grid = (GridView) findViewById(R.id.content);
        home = (RelativeLayout) findViewById(R.id.home);

        home.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("¿Qué quieres añadir?").setItems(R.array.send_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                selectWidget();
                                break;
                            case 1:
                                selectShortcut();
                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

                DragShadow dragShadow = new DragShadow(v);
                ClipData clip = ClipData.newPlainText("", "");
                v.startDrag(clip, dragShadow, v, 0);

                return true;
            }
        });



        drawer = (SlidingDrawer) findViewById(R.id.drawer);
        //For drawing the grid layout with the applications of the system
        //Get the applications info in our array

        //setPacks();
        new LoadApps().execute();

        //GetApps From previous session
        appsLoad();

        botonCambiarFondo = (Button) findViewById(R.id.botonCambiarFondo);
        botonCambiarFondo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LockActivity.class);
                startActivityForResult(intent, REQUEST_PASSWORD);
            }
        });

        //Pregunta para poner como default
        //launchAppChooser();

        /*//Adding widgets
        //As for apps, we need a manager to get the widgets info
        widgetManager = AppWidgetManager.getInstance(this);
        //We create the id.xml in values for creating a resource item of type id
        widgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);*/
    }


    void selectShortcut() {
        Intent intent = new Intent(Intent.ACTION_PICK_ACTIVITY);
        intent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
        startActivityForResult(intent, REQUEST_PICK_SHORTCUT);
    }

    void selectWidget() {
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent, R.id.REQUEST_PICK_APPWIDGET);
    }

    void addEmptyData(Intent pickIntent) {
        ArrayList customInfo = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList customExtras = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == R.id.REQUEST_PICK_APPWIDGET) {
                configureWidget(data);
            } else if (requestCode == R.id.REQUEST_CREATE_APPWIDGET) {
                createWidget(data);
            } else if (requestCode == REQUEST_PICK_SHORTCUT) {
                configureShortcut(data);
            } else if (requestCode == REQUEST_CREATE_SHORTCUT) {
                createShortcut(data);
            }
        } else if (resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    private void configureShortcut(Intent data) {
        startActivityForResult(data, REQUEST_CREATE_SHORTCUT);
    }

    public void createShortcut(Intent intent) {
        Intent.ShortcutIconResource iconResource = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
        Bitmap icon = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
        String shortcutLabel = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        Intent shortIntent = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);

        if (icon == null) {
            if (iconResource != null) {
                Resources resources = null;
                try {
                    resources = packageManager.getResourcesForApplication(iconResource.packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (resources != null) {
                    int id = resources.getIdentifier(iconResource.resourceName, null, null);
                    if (resources.getDrawable(id) instanceof StateListDrawable) {
                        Drawable d = ((StateListDrawable) resources.getDrawable(id)).getCurrent();
                        icon = ((BitmapDrawable) d).getBitmap();
                    } else
                        icon = ((BitmapDrawable) resources.getDrawable(id)).getBitmap();
                }
            }
        }


        if (shortcutLabel != null && shortIntent != null && icon != null) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = 100;
            lp.topMargin = (int) 100;

            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout ll = (LinearLayout) li.inflate(R.layout.drawer_item, null);

            ((ImageView) ll.findViewById(R.id.icon_image)).setImageBitmap(icon);
            ((TextView) ll.findViewById(R.id.icon_text)).setText(shortcutLabel);

            ll.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    v.setOnTouchListener(new MyOnTouchListener());
                    return false;
                }
            });

            ll.setOnClickListener(new ShortcutClickListener(this));
            ll.setTag(shortIntent);
            home.addView(ll, lp);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
    }

    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, R.id.REQUEST_CREATE_APPWIDGET);
        } else {
            createWidget(data);
        }
    }

    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        LauncherAppWidgetHostView hostView = (LauncherAppWidgetHostView) mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);

        hostView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setOnTouchListener(new MyOnTouchListener());
                return true;
            }
        });
        home.addView(hostView);

        drawer.bringToFront();
    }

   /* @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // First we clear the tag to ensure that on every touch down we start with a fresh slate,
        // even in the case where we return early. Not clearing here was causing bugs whereby on
        // long-press we'd end up picking up an item from a previous drag operation.
        final int action = ev.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            //clearTagCellInfo();
        }

        /*if (mInterceptTouchListener != null && mInterceptTouchListener.onTouch(this, ev)) {
            return true;
        }*/
/*
        if (action == MotionEvent.ACTION_DOWN) {
            //setTagToCellInfoForPoint((int) ev.getX(), (int) ev.getY());
        }
        return false;
    }*/

    private void launchAppChooser() {
        //Log.d(TAG, "launchAppChooser()");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public void appsLoad() {
        SerializableData data = Serialization.loadSerializableData();
        if (data != null) {
            for (Pack pack : data.packs) {
                pack.addToHome(this, home);
            }
        }
    }


    public void setPacks() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        //We want to get the apps that could be launch
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //We make a query with the PackageManager for activities launchables using our intent and no flags
        List<ResolveInfo> listPacks = packageManager.queryIntentActivities(mainIntent, 0);
        packs = new Pack[listPacks.size()];
        //We create the packs with the info needed: icons, names, labels
        for (int i = 0; i < listPacks.size(); i++) {
            packs[i] = new Pack();
            packs[i].icon = listPacks.get(i).loadIcon(packageManager);
            packs[i].packageName = listPacks.get(i).activityInfo.packageName;
            packs[i].name = listPacks.get(i).activityInfo.name;
            packs[i].label = listPacks.get(i).loadLabel(packageManager).toString();
        }
        //We can reorder the apps if we want :D

        //initialize the DrawerAdapter with the info
        drawerAdapter = new DrawerAdapter(this, packs);
        //Then just let the adapter do his job on the GridView
        grid.setAdapter(drawerAdapter);
        //We need to make the icons launch the apps with an event. See GridClickListener
        grid.setOnItemClickListener(new GridClickListener(this, packageManager, packs));
        //Long click for putting the icons on home screen
        grid.setOnItemLongClickListener(new GridLongClickListener(this, drawer, home, packs));

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return true;
    }

    private class DragShadow extends View.DragShadowBuilder{

        public DragShadow(View view) {
            super(view);
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
            super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            getView().draw(canvas);
        }
    }


    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()){
            case DragEvent.ACTION_DROP:
                View draggedElement= (View) event.getLocalState();
                home.removeView(draggedElement);
                break;
        }
        return true;
    }

    public class AppListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent arg1) {
            // TODO Auto-generated method stub
            //setPacks();
            new LoadApps().execute();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //DialogFragment.instantiate(this, "Salir");
    }






    public class LoadApps extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> packsList = packageManager.queryIntentActivities(mainIntent, 0);
            packs = new Pack[packsList.size()];
            for (int i = 0; i < packsList.size(); i++) {
                packs[i] = new Pack();
                packs[i].icon = packsList.get(i).loadIcon(packageManager);
                packs[i].packageName = packsList.get(i).activityInfo.packageName;
                packs[i].name = packsList.get(i).activityInfo.name;
                packs[i].label = packsList.get(i).loadLabel(packageManager).toString();

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (drawerAdapter == null) {
                drawerAdapter = new DrawerAdapter(getActivity(), packs);
                grid.setAdapter(drawerAdapter);
                grid.setOnItemClickListener(new GridClickListener(getActivity(), packageManager, packs));
                grid.setOnItemLongClickListener(new GridLongClickListener(getActivity(), drawer, home, packs));
            } else {
                drawerAdapter.packs = packs;
                drawerAdapter.notifyDataSetInvalidated();
            }
        }
    }

}
