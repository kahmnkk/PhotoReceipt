package com.indilist.photoreceipt;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://3.21.35.164:8080/";

    Retrofit retrofit = new Retrofit.Builder()
            //서버 url설정
            .baseUrl(BASE_URL)
            //데이터 파싱 설정
            .addConverterFactory(GsonConverterFactory.create())
            //객체정보 반환
            .build();

    public ApiService apiService = retrofit.create(ApiService.class);
}
