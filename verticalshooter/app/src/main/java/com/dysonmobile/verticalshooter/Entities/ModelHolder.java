package com.dysonmobile.verticalshooter.Entities;

/**
 * Created by stevenstewart on 2/2/15.
 * This class is simply a holder of the model data so that it can be passed from the file access class to class
 * that eventually calls the jni method to load the model data into the native code.
 */
public class ModelHolder
{
    public int vertexCount;
    public int indexCount;
    public float[] vertices;
    public short[] indices;

    public ModelHolder()
    {

    }


}
