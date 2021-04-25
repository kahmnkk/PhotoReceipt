package com.indilist.photoreceipt;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.indilist.photoreceipt.DTO.ResponseDataDTO;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @FormUrlEncoded
    @POST("user/index")
    Call<JsonObject> userJoin(@Field("router") String router,
                              @Field("data") JsonObject data);

    @FormUrlEncoded
    @POST("user/index")
    Call<JsonObject> userLogin(@Field("router") String router,
                               @Field("data") JsonObject data);
}
