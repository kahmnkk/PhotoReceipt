package com.indilist.photoreceipt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.indilist.photoreceipt.DTO.LoginDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    private EditText editTextId, editTextPw;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextId = (EditText) findViewById(R.id.login_id);
        editTextPw = (EditText) findViewById(R.id.login_pw);

        sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
    }

    public void onClickBtnLogin(View v) {
        if (editTextId.getText().toString().equals("") || editTextPw.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
        } else {
            apiLogin(editTextId.getText().toString(), editTextPw.getText().toString());
        }
    }

    public void onClickJoinText(View v) {
        Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
        startActivity(intent);
    }

    public void apiLogin(String id, String pw) {
        RetrofitClient retrofitClient = new RetrofitClient();

        JsonObject requestDTO = new JsonObject();
        requestDTO.addProperty("id", id);
        requestDTO.addProperty("pw", pw);

        Call<JsonObject> call = retrofitClient.apiService.userLogin("login", requestDTO);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    ResponseManager responseManager = new ResponseManager(response.body());
                    if (responseManager.validResponse()) {
                        LoginDTO loginDTO = gson.fromJson(responseManager.getResult(), LoginDTO.class);
                        saveLoginInfo(loginDTO.getIdx(), loginDTO.getNickname());

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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

    public void saveLoginInfo(long idx, String nickname) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("IS_LOGIN", true);
        editor.putLong("USER_IDX", idx);
        editor.putString("USER_NICKNAME", nickname);
        editor.apply();
    }
}