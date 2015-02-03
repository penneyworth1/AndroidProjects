package com.dysonmobile.verticalshooter;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;

import com.dysonmobile.verticalshooter.Entities.ModelHolder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by stevenstewart on 12/27/14.
 */
public class MainRenderer implements GLSurfaceView.Renderer
{
    private native void nativeDrawFrame();
    private native void nativeInitDataModel();
    private native void nativeInitView(float width, float height);
    private native void nativeUpdateWorld(int millies);
    private native void nativeLoadModel(float[] vertices, short[] indices, int vertexCount, int indexCount);

    private long lastUpdateTime = SystemClock.elapsedRealtime();
    private boolean inited = false;
    float screenWidth, screenHeight;
    private Context context;

    public MainRenderer(float screenWidthPar, float screenHeightPar, Context contextPar)
    {
        context = contextPar;
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
            ModelHolder modelHolder = DBandFileAccess.getModelData(context);
            nativeLoadModel(modelHolder.vertices,modelHolder.indices,modelHolder.vertexCount,modelHolder.indexCount);

            nativeInitDataModel();
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
