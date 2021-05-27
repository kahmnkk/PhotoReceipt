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
            + "     vec3 hsv = vec3(0., 0., Cmax);\n"
            + "     if(Cmax > 0.){\n"
            + "         hsv.y = delta/Cmax;\n"
            + "     }\n"
            + "     if(Cmax == rgb.r){\n"
            + "         hsv.x = (rgb.g - rgb.b) / delta;\n"
            + "     }else if(Cmax == rgb.g){\n"
            + "         hsv.x = 2. + (rgb.b - rgb.r) / delta;\n"
            + "     }else{\n"
            + "         hsv.x = 4. + (rgb.r - rgb.g) / delta;\n"
            + "     }\n"
            + "     hsv.x = fract(hsv.x / 6.);\n"
            + "     return hsv;\n"
            + "}\n"
            + "vec3 hsvtorgb(vec3 hsv){\n"
            + "     vec4 K = vec4(1.0, 2.0/3.0, 1.0/3.0, 3.0);\n"
            + "     vec3 P = abs(fract(hsv.xxx + K.xyz) * 6.0 - K.www);\n"
            + "     return hsv.z * mix(K.xxx, clamp(P - K.xxx, 0.0, 1.0), hsv.y);\n"
            + "}\n"
            + "void main() {\n"
            + "  vec4 color = texture2D(sTexture, " +DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ");\n"
            + "  gl_FragColor = brightness_scale * color;\n"
            + "  gl_FragColor = (gl_FragColor - 0.5) * contrast_scale + 0.5;\n"
            + "  vec3 hsv = rgbtohsv(vec3(gl_FragColor.r, gl_FragColor.g, gl_FragColor.b));\n"
            + "  hsv.y = hsv.y * saturation_scale;\n"
            + "  vec3 rgb = hsvtorgb(hsv);\n"
            + "  gl_FragColor = vec4(rgb.x, rgb.y, rgb.z, gl_FragColor.a);\n"
            + "}\n";


    public ProcessingFilter(){}

    private float brightness = 1.0f;
    private float contrast = 1.0f;
    private float saturation = 1.0f;
    private int brightness_location = -1;
    private int contrast_location = -1;
    private int saturation_location = -1;

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public void setContrast(float contrast){
        this.contrast = contrast;
    }

    public void setSaturation(float saturation){
        this.saturation = saturation;
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

    public void setSaturation_param(int percent){
        float param = (float)(percent * 0.02f);
        if(param == 0)param = 0.1f;
        setSaturation(param);
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
        saturation_location = GLES20.glGetUniformLocation(programHandle, "saturation_scale");
    }

    @Override
    protected void onPreDraw(long timestampUs, @NonNull float[] transformMatrix) {
        super.onPreDraw(timestampUs, transformMatrix);
        GLES20.glUniform1f(brightness_location, brightness);
        GLES20.glUniform1f(contrast_location, contrast);
        GLES20.glUniform1f(saturation_location, saturation);
    }

    @NonNull
    @Override
    protected BaseFilter onCopy() {
        ProcessingFilter pf = new ProcessingFilter();
        pf.setBrightness(this.brightness);
        pf.setContrast(this.contrast);
        pf.setSaturation(this.saturation);
        return pf;
    }




}
