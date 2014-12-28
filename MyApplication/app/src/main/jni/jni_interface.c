#include "jni_interface.h"
#include "core.h"

JNIEXPORT jstring JNICALL Java_com_test_ogl2test_MainActivity_getNativeString(JNIEnv *env, jobject thisObj)
{
   return (*env)->NewStringUTF(env, "Hello this can't be working");
}

void Java_com_test_ogl2test_MainRenderer_nativeDrawFrame(JNIEnv *env, jobject thisObj, jint millies)
{
    //__android_log_write(ANDROID_LOG_INFO, "Native c method", "attempting native draw frame");
    renderScene(millies);
}

void Java_com_test_ogl2test_MainRenderer_nativeInitView(JNIEnv *env, jobject thisObj, jfloat width, jfloat height)
{
    __android_log_write(ANDROID_LOG_INFO, "Native c method", "attempting init view");
    initView(width, height);
}