package com.test.ogl2test;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity
{
    static
    {
        System.loadLibrary("MyLib");
    }

    private native String getNativeString();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String test = getNativeString();
        Log.d(test,test);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        MainRenderer mainRenderer = new MainRenderer(metrics.widthPixels,metrics.heightPixels);
        MainSurfaceView mainSurfaceView = new MainSurfaceView(this);
        mainSurfaceView.setEGLContextClientVersion(2);
        mainSurfaceView.setRenderer(mainRenderer);

        setContentView(mainSurfaceView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
