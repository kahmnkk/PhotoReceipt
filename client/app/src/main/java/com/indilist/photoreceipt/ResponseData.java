package com.indilist.photoreceipt;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.indilist.photoreceipt.DTO.ResponseDataDTO;
import com.indilist.photoreceipt.DTO.ResponseResultDTO;

public class ResponseData {
    Gson gson = new Gson();
    ResponseDataDTO responseData = null;

    ResponseData(ResponseDataDTO responseData) {
        this.responseData = responseData;
    }

    public JsonObject getResponse() {
        ResponseResultDTO rtn = gson.fromJson(responseData.getData(), ResponseResultDTO.class);
        return rtn.getResult();
    }
}
