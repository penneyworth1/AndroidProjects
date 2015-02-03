package com.dysonmobile.verticalshooter;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.dysonmobile.verticalshooter.Entities.ModelHolder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by stevenstewart on 1/31/15.
 */
public class DBandFileAccess
{
    public static ModelHolder getModelData(Context context)
    {
        try
        {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("ship2_model.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String dataString = bufferedReader.readLine();
            String[] dataStringChunks = dataString.split(" ");
            int vertexCount = Integer.parseInt(dataStringChunks[0]);
            int indexCount = Integer.parseInt(dataStringChunks[1]);
            String[] verticesString = dataStringChunks[2].split(",");
            String[] indicesString = dataStringChunks[3].split(",");
            float[] vertices = new float[vertexCount*9]; //*9 because there are 9 floats per vertex
            short[] indices = new short[indexCount];

            Log.d("--------","Parsing vertex data");
            for(int i=0;i<vertexCount*9;i++)
                vertices[i] = Float.parseFloat(verticesString[i]);
            Log.d("--------","Parsing index data");
            for(int i=0;i<indexCount;i++)
                indices[i] = Short.parseShort(indicesString[i]);

            ModelHolder modelHolder = new ModelHolder();
            modelHolder.vertexCount = vertexCount;
            modelHolder.indexCount = indexCount;
            modelHolder.vertices = vertices;
            modelHolder.indices = indices;

            Log.d("","");

            return modelHolder;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

}
