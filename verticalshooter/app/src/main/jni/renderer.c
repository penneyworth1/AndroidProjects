//
//  renderer.c
//  verticalshooter
//
//  Created by Steven Stewart on 1/4/15.
//  Copyright (c) 2015 Steven Stewart. All rights reserved.
//

#include "renderer.h"



void buildPerspProjMat(float* m, float fov, float aspect, float znear, float zfar)
{
    float xymax = znear * tan(fov* PI_OVER_360);
    float depth = zfar - znear;
    float q = -(zfar + znear) / depth;
    float qn = -2 * (zfar * znear) / depth;
    float w = znear / (xymax * aspect);
    float h = znear / xymax;
    m[0]  = w; m[1]  = 0; m[2]  = 0;  m[3]  =  0;
    m[4]  = 0; m[5]  = h; m[6]  = 0;  m[7]  =  0;
    m[8]  = 0; m[9]  = 0; m[10] = q;  m[11] = -1;
    m[12] = 0; m[13] = 0; m[14] = qn; m[15] =  0;
}
void setTranslationMatrix(float* m, float tx, float ty, float tz)
{
    m[0]  = 1; m[4]  = 0; m[8]  = 0;  m[12] = tx;
    m[1]  = 0; m[5]  = 1; m[9]  = 0;  m[13] = ty;
    m[2]  = 0; m[6]  = 0; m[10] = 1;  m[14] = tz;
    m[3]  = 0; m[7]  = 0; m[11] = 0;  m[15] =  1;
}
void setRotationMatrx(float* m, const float* q)
{
    xx = q[0] * q[0];
    yy = q[1] * q[1];
    zz = q[2] * q[2];
    xy = q[0] * q[1];
    xz = q[0] * q[2];
    yz = q[1] * q[2];
    wx = q[3] * q[0];
    wy = q[3] * q[1];
    wz = q[3] * q[2];

    m[0]  = 1 - 2 * (yy + zz); m[4] =     2 * (xy - wz); m[8]  =     2 * (xz + wy);  m[12] = 0;
    m[1]  =     2 * (xy + wz); m[5] = 1 - 2 * (xx + zz); m[9]  =     2 * (yz - wx);  m[13] = 0;
    m[2]  =     2 * (xz - wy); m[6] =     2 * (yz + wx); m[10] = 1 - 2 * (xx + yy);  m[14] = 0;
    m[3]  =                 0; m[7] =                 0; m[11] = 0                ;  m[15] = 1;
}
void matMult4x4by4x4(float* m, const float* a, const float* b)
{
    m[0]  = a[0]*b[0]  + a[4]*b[1]  + a[8]* b[2]  + a[12]*b[3];
    m[1]  = a[1]*b[0]  + a[5]*b[1]  + a[9]* b[2]  + a[13]*b[3];
    m[2]  = a[2]*b[0]  + a[6]*b[1]  + a[10]*b[2]  + a[14]*b[3];
    m[3]  = a[3]*b[0]  + a[7]*b[1]  + a[11]*b[2]  + a[15]*b[3];
    m[4]  = a[0]*b[4]  + a[4]*b[5]  + a[8]* b[6]  + a[12]*b[7];
    m[5]  = a[1]*b[4]  + a[5]*b[5]  + a[9]* b[6]  + a[13]*b[7];
    m[6]  = a[2]*b[4]  + a[6]*b[5]  + a[10]*b[6]  + a[14]*b[7];
    m[7]  = a[3]*b[4]  + a[7]*b[5]  + a[11]*b[6]  + a[15]*b[7];
    m[8]  = a[0]*b[8]  + a[4]*b[9]  + a[8]* b[10] + a[12]*b[11];
    m[9]  = a[1]*b[8]  + a[5]*b[9]  + a[9]* b[10] + a[13]*b[11];
    m[10] = a[2]*b[8]  + a[6]*b[9]  + a[10]*b[10] + a[14]*b[11];
    m[11] = a[3]*b[8]  + a[7]*b[9]  + a[11]*b[10] + a[15]*b[11];
    m[12] = a[0]*b[12] + a[4]*b[13] + a[8]* b[14] + a[12]*b[15];
    m[13] = a[1]*b[12] + a[5]*b[13] + a[9]* b[14] + a[13]*b[15];
    m[14] = a[2]*b[12] + a[6]*b[13] + a[10]*b[14] + a[14]*b[15];
    m[15] = a[3]*b[12] + a[7]*b[13] + a[11]*b[14] + a[15]*b[15];
}

GLuint compileShader(const GLchar* shaderSourceString, GLenum shaderType)
{
    GLuint shaderHandle = glCreateShader(shaderType);
    glShaderSource(shaderHandle, 1, &shaderSourceString, NULL);
    glCompileShader(shaderHandle);
    GLint compileSuccess;
    glGetShaderiv(shaderHandle, GL_COMPILE_STATUS, &compileSuccess);
    if (compileSuccess == GL_FALSE)
    {
        GLchar messages[256];
        glGetShaderInfoLog(shaderHandle, sizeof(messages), 0, &messages[0]);
        exit(1);
    }
    return shaderHandle;
}
void compileShaders()
{
    GLchar vertexShaderString[] =
    "attribute vec4 a_position; \n"
    "attribute vec3 a_normal; \n"
    "attribute vec3 a_diffuseMaterial; \n"
    "varying vec3 v_diffuseMaterial; \n"
    "varying vec3 v_rotatedNormal; \n"
    "uniform mat4 u_mvpMatrix; \n"
    "uniform mat4 u_rotMatrix; \n"
    "void main(void) \n"
    "{ \n"
    "    v_rotatedNormal = normalize(vec3(u_rotMatrix * vec4(a_normal, 0.0))); \n"
    "    v_diffuseMaterial = a_diffuseMaterial; \n"
    "    gl_Position = u_mvpMatrix * a_position; \n"
    "} \0";

    GLchar fragmentShaderString[] =
    "varying lowp vec3 v_diffuseMaterial; \n"
    "varying lowp vec3 v_rotatedNormal; \n"
    "uniform lowp vec3 u_lightDirection; \n"
    "void main(void) \n"
    "{ \n"
    "    highp vec3 L = normalize(u_lightDirection); \n"
    "    highp float df = max(0.0, dot(v_rotatedNormal, -L)); \n"

    //For toon Shading
    //"    if (df < 0.1) df = 0.0; \n"
    //"    else if (df < 0.5) df = 0.5; \n"
    //"    else df = 1.0; \n"

    "    highp vec3 color = df * v_diffuseMaterial; \n"
    "    gl_FragColor = vec4(color, 1); \n"
    "} \0";

    GLuint compiledVertexShaderReference = compileShader(vertexShaderString, GL_VERTEX_SHADER);
    GLuint compiledFragmentShaderReference = compileShader(fragmentShaderString, GL_FRAGMENT_SHADER);
    GLuint programHandle = glCreateProgram();
    glAttachShader(programHandle, compiledVertexShaderReference);
    glAttachShader(programHandle, compiledFragmentShaderReference);
    glLinkProgram(programHandle);
    GLint linkSuccess;
    glGetProgramiv(programHandle, GL_LINK_STATUS, &linkSuccess);
    if (linkSuccess == GL_FALSE)
    {
        GLchar messages[256];
        glGetProgramInfoLog(programHandle, sizeof(messages), 0, &messages[0]);
        exit(1);
    }
    glUseProgram(programHandle);
    _positionSlot = glGetAttribLocation(programHandle, "a_position");
    _normalSlot = glGetAttribLocation(programHandle, "a_normal");
    _colorSlot = glGetAttribLocation(programHandle, "a_diffuseMaterial");
    uniform_mvpMatrix = glGetUniformLocation(programHandle, "u_mvpMatrix");
    uniform_rotMatrix = glGetUniformLocation(programHandle, "u_rotMatrix");

    uniform_lightDirection = glGetUniformLocation(programHandle, "u_lightDirection");

    //    uniform_ambientMaterial = glGetUniformLocation(programHandle, "u_AmbientMaterial");
    //    uniform_lightPosition = glGetUniformLocation(programHandle, "u_LightPosition");
    //    uniform_specularMaterial = glGetUniformLocation(programHandle, "u_SpecularMaterial");
    //    uniform_shininess = glGetUniformLocation(programHandle, "u_Shininess");

    glEnableVertexAttribArray(_positionSlot);
    glEnableVertexAttribArray(_normalSlot);
    glEnableVertexAttribArray(_colorSlot);
}

void setupVBOs()
{
    GLuint vertexBuffer;
    glGenBuffers(1, &vertexBuffer);
    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, sizeof(Vertex)*theModel.VertexCount, theModel.Vertices, GL_STATIC_DRAW);

    GLuint indexBuffer;
    glGenBuffers(1, &indexBuffer);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(GLushort)*theModel.IndexCount, theModel.Indices, GL_STATIC_DRAW);
}

void initView(float screenWidthInPixelsPar, float screenHeightInPixelsPar)
{
    compileShaders();
    setupVBOs();

    screenWidthInPixels = screenWidthInPixelsPar;
    screenHeightInPixels = screenHeightInPixelsPar;

    float fov=30.0f; // in degrees
    float aspect=((float)screenWidthInPixels)/screenHeightInPixels;
    float znear=10.0f;
    float zfar=10000.0f;
    buildPerspProjMat(perspectiveMatrix, fov, aspect, znear, zfar);
    glViewport(0, 0, screenWidthInPixels, screenHeightInPixels);

    glClearColor(0, 50.0/255.0, 254.0/255.0, 1.0);
    glEnable(GL_DEPTH_TEST);

    //Normalize rot axis
    float axisNorm = sqrtf(rotAxis[0]*rotAxis[0] + rotAxis[1]*rotAxis[1] + rotAxis[2]*rotAxis[2]);
    rotAxis[0] =  rotAxis[0] / axisNorm;
    rotAxis[1] =  rotAxis[1] / axisNorm;
    rotAxis[2] =  rotAxis[2] / axisNorm;

    glEnable(GL_CULL_FACE);
    glFrontFace(GL_CCW);
    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
    glEnable(GL_BLEND);
}

void renderScene()
{
    //__android_log_write(ANDROID_LOG_INFO, "Native c method", "rederer: renderScene starting");
    //angle += 0.001 * timeDiffMillies; if(angle > 2*pi) angle = 0;

    //Build a quaternion for the current angle.
    float sinHalfAngle = sinf(angle/2);
    quat[3] = cosf(angle/2);
    quat[0] = rotAxis[0] * sinHalfAngle;
    quat[1] = rotAxis[1] * sinHalfAngle;
    quat[2] = rotAxis[2] * sinHalfAngle;

    setRotationMatrx(rotationMatrix, quat);
    setTranslationMatrix(translationMatrix, cos(angle), sin(angle), -40 + 18*sin(angle));

    //Combine transformation matrices so the shader does not recalculate the combined transformation for each vertex.
    matMult4x4by4x4(transRotMatrix, translationMatrix, rotationMatrix);
    matMult4x4by4x4(modelMatrix, perspectiveMatrix, transRotMatrix);

    //Upload transformation matrix to shader.
    glUniformMatrix4fv(uniform_rotMatrix, 1, 0, rotationMatrix);
    glUniformMatrix4fv(uniform_mvpMatrix, 1, 0, modelMatrix);

    //Lighting uniforms
    glUniform3f(uniform_lightDirection, .3, -.1, -.1);
    //glUniform3f(uniform_lightPosition, 0, 10, -1);
    //glUniform3f(uniform_ambientMaterial, 1, 0, 0);
    //glUniform3fv(uniform_specularMaterial, 1, specularMaterial);
    //glUniform1f(uniform_shininess, shininess);

    glVertexAttribPointer(_positionSlot, 3, GL_FLOAT, GL_FALSE, sizeof(Vertex), 0);
    glVertexAttribPointer(_normalSlot, 3, GL_FLOAT, GL_FALSE, sizeof(Vertex), (GLvoid*) (sizeof(GLfloat) * 3));
    glVertexAttribPointer(_colorSlot, 3, GL_FLOAT, GL_FALSE, sizeof(Vertex), (GLvoid*) (sizeof(GLfloat) * 6));

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glDrawElements(GL_TRIANGLES, theModel.IndexCount, GL_UNSIGNED_SHORT, 0);
}