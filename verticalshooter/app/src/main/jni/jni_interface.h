#include <jni.h>
#include <android/log.h>

JNIEXPORT jstring JNICALL Java_com_dysonmobile_verticalshooter_MainActivity_getNativeString(JNIEnv *, jobject);

void Java_com_dysonmobile_verticalshooter_MainRenderer_nativeDrawFrame(JNIEnv*, jobject);
void Java_com_dysonmobile_verticalshooter_MainRenderer_nativeUpdateWorld(JNIEnv*, jobject, jint);
void Java_com_dysonmobile_verticalshooter_MainRenderer_nativeInitView(JNIEnv*, jobject, jfloat, jfloat);
void Java_com_dysonmobile_verticalshooter_MainRenderer_nativeInitDataModel(JNIEnv*, jobject);