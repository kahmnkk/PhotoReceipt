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
            + "uniform float contrast_scale;\n"
            + "uniform float brightness_scale;\n"
            + "varying vec2 " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME+";\n"
            + "vec3 rgbtohsv(vec3 rgb){\n"
            + "     float Cmax = max(rgb.r, max(rgb.g, rgb.b));\n"
            + "     float Cmin = min(rgb.r, min(rgb.g, rgb.b));\n"
            + "     float delta = Cmax - Cmin;\n"
            + "     vec3 = hsv(0., 0., Cmax);\n"
            + "     if(Cmax > 0){\n"
            + "         hsv.y = delta/Cmax;\n"
            + "     }\n"
            + "}\n"
            + "void main() {\n"
            + "  vec4 color = texture2D(sTexture, " +DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ");\n"
            + "  gl_FragColor = brightness_scale * color;\n"
            + "  gl_FragColor = (gl_FragColor - 0.5) * contrast_scale + 0.5;\n"
            + "}\n";


    //Brightness와 Saturation 조정 가능한 Fragment shader...
    //Saturation은 다른 shader 예제를 더 찾아 보아야 함

    public ProcessingFilter(){}

    private float brightness = 1.0f;
    private float contrast = 1.0f;
    private int brightness_location = -1;
    private int contrast_location = -1;

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public void setContrast(float contrast){
        this.contrast = contrast;
    }

    public void setBrightness_param(int percent){
        float param = (float)(percent * 0.02f);
        if(param == 0)param = 0.1f;
        setBrightness(param);
    }

    public void setContrast_param(int percent){
        float param = (float)(percent * 0.02f);
        if(param == 0)param = 0.1f;
        setContrast(param);
    }

    @NonNull
    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }

    @Override
    public void onCreate(int programHandle) {
        super.onCreate(programHandle);
        brightness_location = GLES20.glGetUniformLocation(programHandle, "brightness_scale");
        contrast_location = GLES20.glGetUniformLocation(programHandle, "contrast_scale");
    }

    @Override
    protected void onPreDraw(long timestampUs, @NonNull float[] transformMatrix) {
        super.onPreDraw(timestampUs, transformMatrix);
        GLES20.glUniform1f(brightness_location, brightness);
        GLES20.glUniform1f(contrast_location, contrast);
    }
}
