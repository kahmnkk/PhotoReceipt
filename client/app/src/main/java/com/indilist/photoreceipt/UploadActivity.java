package com.indilist.photoreceipt;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    private static final int REQUEST_GALLERY = 1111;

    private Uri imageURI;
    private String imagePath;
    private ImageView imageView;
    private Long idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        SharedPreferences userInfo = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        idx = userInfo.getLong("USER_IDX", 0);

        imageView = (ImageView) findViewById(R.id.img_gallery);
    }

    public void onClickGallery(View view) {
        getImageFromGallery();
    }

    public void onClickBtnUpload(View view) {
        apiUpload();
    }

    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    imageURI = data.getData(); // 이미지 경로
                    imageView.setImageURI(imageURI);
                    imagePath = getRealPathFromURI(imageURI, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getRealPathFromURI(Uri contentUri, Context activity) {
        String path = null;
        try {
            final String[] proj = {MediaStore.MediaColumns.DATA};
            final Cursor cursor = ((Activity) activity).managedQuery(contentUri, proj, null, null, null);
            final int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
        } catch (Exception e) {
        }
        if (path != null && path.length() > 0) {
            return path;
        } else return contentUri.getPath();
    }

    public void apiUpload() {
        if (imageURI != null) {
            File img = new File(imagePath);

            RequestBody imgReq = RequestBody.create(MediaType.parse("image/*"), img);
            MultipartBody.Part imgToUpload = MultipartBody.Part.createFormData("image", img.getName(), imgReq);

            RequestBody userIdxReq = RequestBody.create(MediaType.parse("text/plain"), idx.toString());

            JsonObject filterObj = new JsonObject();
            RequestBody filterReq = RequestBody.create(MediaType.parse("text/plain"), filterObj.toString());

            RequestBody textReq = RequestBody.create(MediaType.parse("text/plain"), "ㅇ아아ㅏㅏ아ㅏ");

            RetrofitClient retrofitClient = new RetrofitClient();
            Call<JsonObject> call = retrofitClient.apiService.communityUpload(imgToUpload, userIdxReq, filterReq, textReq);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        ResponseManager responseManager = new ResponseManager(response.body());
                        if (responseManager.validResponse()) {
                            Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            responseManager.errorHandler(getApplicationContext());
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    System.out.println(t.getMessage());
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(UploadActivity.this, "업로드할 이미지를 선택해주세요", Toast.LENGTH_LONG).show();
        }
    }
}