#include "native_code.h"
#include <stdlib.h>
#include <stdio.h>
#include <stdarg.h>
#include <string.h>
#include <math.h>
#include <unistd.h>

JNIEXPORT jstring JNICALL Java_stevenstewart_programmingtest_nike_MainActivity_getNativeString(JNIEnv *env, jobject thisObj)
{
   return (*env)->NewStringUTF(env, "Hello this can't be working");
}

