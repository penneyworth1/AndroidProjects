//
//  datamodel.h
//  verticalshooter
//
//  Created by Steven Stewart on 1/4/15.
//  Copyright (c) 2015 Steven Stewart. All rights reserved.
//

#ifndef __verticalshooter__datamodel__
#define __verticalshooter__datamodel__

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

#endif /* defined(__verticalshooter__datamodel__) */

#ifndef STRUCTS_AND_VARS
#define STRUCTS_AND_VARS
typedef struct
{
    GLfloat x,y,z,nx,ny,nz,r,g,b;
}Vertex;
typedef struct
{
    int VertexCount;
    int IndexCount;
    Vertex* Vertices;
    GLushort* Indices;
} Model;

#define pi 3.14159265358979323846264338327
#define PI_OVER_360 0.0087266

//Testing
Model theModel;

//Shader variable references
GLuint _positionSlot;
GLuint _colorSlot;
GLuint _normalSlot;
GLuint uniform_mvpMatrix;
GLuint uniform_rotMatrix;
GLuint uniform_ambientMaterial;
GLuint uniform_lightPosition;
GLuint uniform_specularMaterial;
GLuint uniform_shininess;
GLuint uniform_lightDirection;

GLfloat screenWidthInPixels;
GLfloat screenHeightInPixels;

float timeDiff;
float lastTimeStamp;

float modelMatrix[16];
float perspectiveMatrix[16];
float translationMatrix[16];
float rotationMatrix[16];
float transRotMatrix[16]; //To store the result of the translation matrix multiplied by the rotation matrix
float angle;
float rotAxis[3];
float quat[4]; //rotation quaternion
float xx,yy,zz,xy,xz,yz,wx,wy,wz;

void initDataModel();

#endif




