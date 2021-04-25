package com.indilist.photoreceipt.DTO;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseResultDTO {
    @SerializedName("result")
    @Expose
    private JsonObject result;

    public JsonObject getResult() {
        return result;
    }

    public void setResult(JsonObject result) {
        this.result = result;
    }
}
