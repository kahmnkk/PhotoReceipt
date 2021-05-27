package com.indilist.photoreceipt.ui.camera;

import android.opengl.GLES20;
import android.util.Log;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filter.BaseFilter;

public class ProcessingFilter extends BaseFilter

{

    private final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n"
            + "precision mediump float;\n"
            + "uniform samplerExternalOES sTexture;\n"
            + "uniform float saturation_scale;\n"
            + "uniform float contrast_scale;\n"
            + "uniform float brightness_scale;\n"
            + "uniform float rboost_scale;\n"
            + "uniform float gboost_scale;\n"
            + "uniform float bboost_scale;\n"
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
            + "  if(gl_FragColor.r > 0.5 && gl_FragColor.g < 0.5 && gl_FragColor.b < 0.5){\n"
            + "     gl_FragColor.r = gl_FragColor.r * rboost_scale;\n"
            + "  }\n"
            + "  if(gl_FragColor.g > 0.5 && gl_FragColor.r < 0.5 && gl_FragColor.b < 0.5){\n"
            + "     gl_FragColor.g = gl_FragColor.g * gboost_scale;\n"
            + "  }\n"
            + "  if(gl_FragColor.b > 0.5 && gl_FragColor.r < 0.5 && gl_FragColor.g < 0.5){\n"
            + "  gl_FragColor.b = gl_FragColor.b * bboost_scale;\n"
            + "  }\n"
            + "}\n";


    public ProcessingFilter(){}

    private float brightness = 1.0f;
    private float contrast = 1.0f;
    private float saturation = 1.0f;
    private float rboost = 1.0f;
    private float gboost = 1.0f;
    private float bboost = 1.0f;
    private int brightness_location = -1;
    private int contrast_location = -1;
    private int saturation_location = -1;
    private int rboost_location = -1;
    private int gboost_location = -1;
    private int bboost_location = -1;



    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public void setContrast(float contrast){
        this.contrast = contrast;
    }

    public void setSaturation(float saturation){
        this.saturation = saturation;
    }

    public void setRboost(float rboost){
        this.rboost = rboost;
    }

    public void setGboost(float gboost){
        this.gboost = gboost;
    }
    public void setBboost(float bboost){
        this.bboost = bboost;
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

    public void setRboost_param(int percent){
        float param = (float)(percent * 0.02f);
        if(param == 0)param = 0.1f;
        setRboost(param);
    }
    public void setGboost_param(int percent){
        float param = (float)(percent * 0.02f);
        if(param == 0)param = 0.1f;
        setGboost(param);
    }
    public void setBboost_param(int percent){
        float param = (float)(percent * 0.02f);
        if(param == 0)param = 0.1f;
        setBboost(param);
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
        rboost_location = GLES20.glGetUniformLocation(programHandle, "rboost_scale");
        gboost_location = GLES20.glGetUniformLocation(programHandle, "gboost_scale");
        bboost_location = GLES20.glGetUniformLocation(programHandle, "bboost_scale");

    }

    @Override
    protected void onDraw(long timestampUs) {
        super.onDraw(timestampUs);


    }



    @Override
    protected void onPreDraw(long timestampUs, @NonNull float[] transformMatrix) {
        super.onPreDraw(timestampUs, transformMatrix);
        GLES20.glUniform1f(brightness_location, brightness);
        GLES20.glUniform1f(contrast_location, contrast);
        GLES20.glUniform1f(saturation_location, saturation);
        GLES20.glUniform1f(rboost_location, rboost);
        GLES20.glUniform1f(gboost_location, gboost);
        GLES20.glUniform1f(bboost_location, bboost);

    }



    @NonNull
    @Override
    protected BaseFilter onCopy() {
        ProcessingFilter pf = new ProcessingFilter();
        pf.setBrightness(this.brightness);
        pf.setContrast(this.contrast);
        pf.setSaturation(this.saturation);
        pf.setRboost(this.rboost);
        pf.setGboost(this.gboost);
        pf.setBboost(this.bboost);
        return pf;
    }










}
