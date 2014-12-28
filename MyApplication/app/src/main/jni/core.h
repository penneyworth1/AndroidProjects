//Android specific includes
#include <GLES2/gl2.h>
#include <android/log.h>
///////////////////////////

#include <stdlib.h>
#include <stdio.h>
#include <stdarg.h>
#include <string.h>
#include <math.h>
#include <unistd.h>
#include <time.h>

void renderScene(int timeDiffMillies);
void initView(float screenWidthInPixelsPar, float screenHeightInPixelsPar);