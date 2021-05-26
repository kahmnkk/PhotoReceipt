package com.indilist.photoreceipt.DTO;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseListDTO {
    @SerializedName("list")
    @Expose
    private JsonArray list;

    public JsonArray getList() {
        return list;
    }

    public void setList(JsonArray list) {
        this.list = list;
    }
}
