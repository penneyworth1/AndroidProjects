#include "jni_interface.h"
#include "renderer.h"
#include "controller.h"

JNIEXPORT jstring JNICALL Java_com_dysonmobile_verticalshooter_MainActivity_getNativeString(JNIEnv *env, jobject thisObj)
{
   return (*env)->NewStringUTF(env, "Hello this can't be working");
}

void Java_com_dysonmobile_verticalshooter_MainRenderer_nativeDrawFrame(JNIEnv *env, jobject thisObj)
{
    renderScene();
}
void Java_com_dysonmobile_verticalshooter_MainRenderer_nativeUpdateWorld(JNIEnv *env, jobject thisObj, jint millies)
{
    updateWorld((int)millies);
}
void Java_com_dysonmobile_verticalshooter_MainRenderer_nativeInitView(JNIEnv *env, jobject thisObj, jfloat width, jfloat height)
{
    initView((float)width, (float)height);
}
void Java_com_dysonmobile_verticalshooter_MainRenderer_nativeInitDataModel(JNIEnv *env, jobject thisObj)
{
    initDataModel();
}

void Java_com_dysonmobile_verticalshooter_MainRenderer_nativeLoadModel(JNIEnv* env, jobject javaThis, jfloatArray verticesPar, jshortArray indicesPar, jint vertexCount, jint indexCount)
{
    __android_log_write(ANDROID_LOG_INFO, "Native c method", "native load model starting");

    jfloat* vertexData = (*env)->GetFloatArrayElements(env, verticesPar, 0);
    jshort* indexData = (*env)->GetShortArrayElements(env, indicesPar, 0);

    GLfloat* vertexArray = malloc(sizeof(GLfloat) * (vertexCount*9));
    GLushort* indexArray = malloc(sizeof(GLushort) * indexCount);

    int i;
    for(i=0;i<vertexCount*9;i++)
    {
        vertexArray[i] = (GLfloat)vertexData[i];
    }
    for(i=0;i<indexCount;i++)
    {
        indexArray[i] = (GLushort)indexData[i];
    }

    Vertex* vertices = malloc(sizeof(Vertex)*vertexCount);
    for(i=0;i<vertexCount;i++)
    {
        vertices[i].x  = vertexArray[i*9+0];
        vertices[i].y  = vertexArray[i*9+1];
        vertices[i].z  = vertexArray[i*9+2];
        vertices[i].nx = vertexArray[i*9+3];
        vertices[i].ny = vertexArray[i*9+4];
        vertices[i].nz = vertexArray[i*9+5];
        vertices[i].r  = vertexArray[i*9+6];
        vertices[i].g  = vertexArray[i*9+7];
        vertices[i].b  = vertexArray[i*9+8];

        //__android_log_print(ANDROID_LOG_INFO, "*-*-*-*", "vert x = %f", vertices[i].x);
        //__android_log_print(ANDROID_LOG_INFO, "*-*-*-*", "vert b = %f", vertices[i].b);
    }

    //GLushort* indices = malloc(sizeof(GLushort)*indexCount);
    //for(int i=0;i<indexCount;i++)
        //indices[i] = (GLushort)[((NSString*)[indexStrings objectAtIndex:i]) intValue];

    free(vertexArray);
    //free(indexArray); //We'll just pass this array

    loadModel((int)vertexCount, (int)indexCount, vertices, indexArray);
}


