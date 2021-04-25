package com.indilist.photoreceipt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    private EditText editTextId, editTextPw, editTextPwCheck, editTextNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        editTextId = (EditText) findViewById(R.id.join_id);
        editTextPw = (EditText) findViewById(R.id.join_pw);
        editTextPwCheck = (EditText) findViewById(R.id.join_pw_check);
        editTextNickname = (EditText) findViewById(R.id.join_nickname);
    }

    public void onClickBtnJoin(View v) {
        if (editTextId.getText().toString().equals("") || editTextPw.getText().toString().equals("")
                || editTextPwCheck.getText().toString().equals("") || editTextNickname.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "정보를 정확히 기입해주세요", Toast.LENGTH_SHORT).show();
        } else if (!editTextPw.getText().toString().equals(editTextPwCheck.getText().toString())) {
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
        } else {
            apiJoin(editTextId.getText().toString(), editTextPw.getText().toString(), editTextNickname.getText().toString());
        }
    }

    public void apiJoin(String id, String pw, String nickname) {
        RetrofitClient retrofitClient = new RetrofitClient();

        JsonObject requestDTO = new JsonObject();
        requestDTO.addProperty("id", id);
        requestDTO.addProperty("pw", pw);
        requestDTO.addProperty("nickname", nickname);

        Call<JsonObject> call = retrofitClient.apiService.userJoin("join", requestDTO);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    ResponseManager responseManager = new ResponseManager(response.body());
                    if (responseManager.validResponse()) {
                        Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
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
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}