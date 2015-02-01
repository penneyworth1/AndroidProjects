#include "datamodel.h"

void initDataModel()
{
    timeDiff = 0.0f;
    lastTimeStamp = 0;

    angle = 0;
    rotAxis[0] = .4; //{1,1,1};
    rotAxis[1] = .5;
    rotAxis[2] = 1;
}