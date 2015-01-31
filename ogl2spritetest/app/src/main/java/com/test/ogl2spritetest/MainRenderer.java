package com.test.ogl2spritetest;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.SystemClock;
import android.util.Log;

import java.io.InputStream;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by stevenstewart on 12/27/14.
 */
public class MainRenderer implements GLSurfaceView.Renderer
{
    private native void nativeDrawFrame(int millies);
    private native void nativeInitView(float width, float height);
    private native void nativeSetTextureReference(int texReference);

    private long lastUpdateTime = SystemClock.elapsedRealtime();
    private boolean inited = false;
    float screenWidth, screenHeight;
    Context context;
    private FloatBuffer mTextureBuffer = null;
    private int [] textureReferences = null;

    public MainRenderer(float screenWidthPar, float screenHeightPar, Context contextPar)
    {
        screenWidth = screenWidthPar;
        screenHeight = screenHeightPar;
        context = contextPar;
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
            getTexture();

            inited = true;
        }

        long systemTime = SystemClock.elapsedRealtime();
        long deltaTime = (int)(systemTime - lastUpdateTime);
        lastUpdateTime = SystemClock.elapsedRealtime();
        if(deltaTime > 100) deltaTime = 100;

        nativeDrawFrame((int)deltaTime);
    }

    private void getTexture()
    {
        AssetManager assetManager = context.getAssets();
        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            InputStream inputStream = assetManager.open("run_sprite_opt.png");
            Bitmap b1 = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            GLES20.glEnable(GLES20.GL_TEXTURE_2D);
            textureReferences = new int[1];
            GLES20.glDeleteTextures(textureReferences.length,textureReferences,0);
            GLES20.glFlush();
            GLES20.glGenTextures(1,textureReferences,0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureReferences[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, b1, 0);

            nativeSetTextureReference(textureReferences[0]);

            b1.recycle();

            Log.d("", "");
        }
        catch (Exception ex)
        {
            Log.d("","");
        }
    }

}
