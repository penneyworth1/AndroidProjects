#include "controller.h"

//Returns an int representing a command type for the wrapper to execute.
int updateWorld(int timeDiffMillies)
{
    angle += 0.001 * timeDiffMillies; if(angle > 2*pi) angle = 0;

    return 0;
}

void loadModel(int vertexCount, int indexCount, Vertex* vertexComponents, GLushort* indices)
{
    __android_log_write(ANDROID_LOG_INFO, "Native c method", "controller: loadModel starting");

    free(theModel.Vertices);
    free(theModel.Indices);
    theModel.VertexCount = vertexCount;
    theModel.IndexCount = indexCount;
    theModel.Vertices = vertexComponents;
    theModel.Indices = indices;
}