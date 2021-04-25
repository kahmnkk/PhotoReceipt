package com.indilist.photoreceipt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.indilist.photoreceipt.DTO.LoginDTO;
import com.indilist.photoreceipt.DTO.ResponseDataDTO;
import com.indilist.photoreceipt.DTO.ResponseResultDTO;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onClickBtnJoin(View v) {
        apiJoin();
    }

    public void onClickBtnLogin(View v) {
        apiLogin();
    }

    public void apiJoin() {
        RetrofitClient retrofitClient = new RetrofitClient();

        JsonObject requestDTO = new JsonObject();
        requestDTO.addProperty("id", "qwer");
        requestDTO.addProperty("pw", "1234");
        requestDTO.addProperty("nickname", "aaaa");

        Call<JsonObject> call = retrofitClient.apiService.userJoin("join", requestDTO);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    System.out.println(response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void apiLogin() {
        RetrofitClient retrofitClient = new RetrofitClient();

        JsonObject requestDTO = new JsonObject();
        requestDTO.addProperty("id", "qwer");
        requestDTO.addProperty("pw", "1234");

        Call<ResponseDataDTO> call = retrofitClient.apiService.userLogin("login", requestDTO);
        call.enqueue(new Callback<ResponseDataDTO>() {
            @Override
            public void onResponse(Call<ResponseDataDTO> call, Response<ResponseDataDTO> response) {
                if (response.isSuccessful()) {
                    ResponseData result = new ResponseData(response.body());
                    LoginDTO loginDTO = gson.fromJson(result.getResponse(), LoginDTO.class);
                    System.out.println(loginDTO.getNickname());
                    System.out.println(loginDTO.getIdx());
                }
            }

            @Override
            public void onFailure(Call<ResponseDataDTO> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}