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


