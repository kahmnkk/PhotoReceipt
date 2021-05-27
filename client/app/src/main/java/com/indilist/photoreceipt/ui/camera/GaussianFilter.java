package com.indilist.photoreceipt.ui.camera;

import android.opengl.GLES20;
import android.util.Log;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.filter.BaseFilter;

public class GaussianFilter extends BaseFilter {

    private final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n"
            + "precision mediump float;\n"
            + "uniform samplerExternalOES sTexture;\n"
            + "uniform sampler2D texture0;\n"
            + "uniform int pass;\n"
            + "varying vec2 " + DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME+";\n"
            + "void main() {\n"
            + "  vec4 color = texture2D(sTexture, " +DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ");\n"
            + "  vec4 colort = texture2D(texture0, " +DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME + ");\n"
            + "  ivec2 pix = ivec2("+DEFAULT_FRAGMENT_TEXTURE_COORDINATE_NAME+".xy);\n"
            + "  vec4 sum = texelFetch(texture0, pix, 0) * 3;\n"
            + "  gl_FragColor = sum;\n"
            + "}\n";

    private int[] framebuffer = new int[2];
    private int[] texture = new int[2];
    private int pass_location = -1;
    private int texture_location = -1;
    private int pass = 0;
    @NonNull
    @Override
    public String getFragmentShader() {
        return FRAGMENT_SHADER;
    }

    public GaussianFilter() {}

    @Override
    public void onCreate(int programHandle) {
        super.onCreate(programHandle);
        pass_location = GLES20.glGetUniformLocation(programHandle, "pass");
        texture_location = GLES20.glGetUniformLocation(programHandle, "texture0");

        GLES20.glGenFramebuffers(1, framebuffer, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer[0]);

        GLES20.glGenTextures(1, texture, 0) ;
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA4, 1080, 1920, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture[0], 0);

        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);

        if(status != GLES20.GL_FRAMEBUFFER_COMPLETE){
            Log.d("FBORenderer", "Framebuffer incomplete. status: " + status);

            throw new RuntimeException("Error creating FBO");
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);


    }

    @Override
    protected void onDraw(long timestampUs) {
        super.onDraw(timestampUs);
        //GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer[0]);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        GLES20.glUniform1i(texture_location, 0);

    }

    @NonNull
    @Override
    protected BaseFilter onCopy() {
        return super.onCopy();
    }

    @Override
    protected void onPreDraw(long timestampUs, @NonNull float[] transformMatrix) {
        super.onPreDraw(timestampUs, transformMatrix);
        GLES20.glUniform1i(pass_location, pass);
    }
}
