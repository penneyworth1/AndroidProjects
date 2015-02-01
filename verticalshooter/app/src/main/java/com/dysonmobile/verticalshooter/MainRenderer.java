package com.dysonmobile.verticalshooter;

import android.opengl.GLSurfaceView;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by stevenstewart on 12/27/14.
 */
public class MainRenderer implements GLSurfaceView.Renderer
{
    private native void nativeDrawFrame();
    private native void nativeInitView(float width, float height);
    private native void nativeUpdateWorld(int millies);

    private long lastUpdateTime = SystemClock.elapsedRealtime();
    private boolean inited = false;
    float screenWidth, screenHeight;

    public MainRenderer(float screenWidthPar, float screenHeightPar)
    {
        screenWidth = screenWidthPar;
        screenHeight = screenHeightPar;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {

    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        if(!inited)
        {
            nativeInitView(screenWidth, screenHeight);
            inited = true;
        }

        long systemTime = SystemClock.elapsedRealtime();
        long deltaTime = (int)(systemTime - lastUpdateTime);
        lastUpdateTime = SystemClock.elapsedRealtime();
        if(deltaTime > 100) deltaTime = 100;

        nativeUpdateWorld((int)deltaTime);
        nativeDrawFrame();
    }
}
