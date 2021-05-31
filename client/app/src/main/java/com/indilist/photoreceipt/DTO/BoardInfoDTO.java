package com.indilist.photoreceipt.DTO;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoardInfoDTO {
    @SerializedName("boardInfo")
    @Expose
    private JsonObject boardInfo;
    @SerializedName("ownerInfo")
    @Expose
    private JsonObject ownerInfo;
    @SerializedName("isLiked")
    @Expose
    private Boolean isLiked;

    public JsonObject getBoardInfo() {
        return boardInfo;
    }

    public void setBoardInfo(JsonObject boardInfo) {
        this.boardInfo = boardInfo;
    }

    public JsonObject getOwnerInfo() {
        return ownerInfo;
    }

    public void setOwnerInfo(JsonObject ownerInfo) {
        this.ownerInfo = ownerInfo;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }
}
