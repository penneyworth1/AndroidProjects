#include <jni.h>
#include <android/log.h>

JNIEXPORT jstring JNICALL Java_com_test_ogl2test_MainActivity_getNativeString(JNIEnv *, jobject);
void Java_com_test_ogl2test_MainRenderer_nativeDrawFrame(JNIEnv*, jobject, jint);
void Java_com_test_ogl2test_MainRenderer_nativeInitView(JNIEnv*, jobject, jfloat, jfloat);