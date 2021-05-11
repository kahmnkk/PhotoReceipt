package com.indilist.photoreceipt.ui.camera;

import android.opengl.GLES20;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filter.BaseFilter;
import com.otaliastudios.cameraview.filter.TwoParameterFilter;

public class ProcessingFilter extends BaseFilter

{

    private final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n"
            + "precision mediump float;\n"
            + "uniform samplerExternalOES sTexture;\n"
            + "uniform float saturation_scale;\n"
            + "uniform float brightness_scale;\n"
            + "varying vec2" + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME+";\n"
            + "void main() {\n"
            + "  vec4 color = texture2D(sTexture, " +DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ");\n"
            + "  gl_FragColor = brightness_scale"
            + "}\n";


    //Brightness와 Saturation 조정 가능한 Fragment shader...
    //Saturation은 다른 shader 예제를 더 찾아 보아야 함

    public ProcessingFilter(){}

    private float brightness = 1.0f;
    private int brightness_param = 0; // default to 0, 좌우 게이지 조절에 따라 -1 ~ +1 사이 조정. 0이 디폴트
    private int brightness_location = -1;

    @NonNull
    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }

    @Override
    public void onCreate(int programHandle) {
        super.onCreate(programHandle);
        brightness_location = GLES20.glGetUniformLocation(programHandle, "brightness_scale");
    }

    @Override
    protected void onPreDraw(long timestampUs, @NonNull float[] transformMatrix) {
        super.onPreDraw(timestampUs, transformMatrix);
        GLES20.glUniform1f(brightness_location, brightness);
    }
}
