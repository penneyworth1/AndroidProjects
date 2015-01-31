#include "jni_interface.h"

JNIEXPORT jstring JNICALL Java_com_dysonmobile_verticalshooter_MainActivity_getNativeString(JNIEnv *env, jobject thisObj)
{
   return (*env)->NewStringUTF(env, "Hello this can't be working");
}
