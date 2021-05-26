package com.indilist.photoreceipt.DTO;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoardListDTO {
    @SerializedName("idx")
    @Expose
    private long idx;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("imgLink")
    @Expose
    private String imgLink;
    @SerializedName("filter")
    @Expose
    private JsonObject filter;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("like")
    @Expose
    private Integer like;

    public long getIdx() {
        return idx;
    }

    public void setIdx(long idx) {
        this.idx = idx;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public JsonObject getFilter() {
        return filter;
    }

    public void setFilter(JsonObject filter) {
        this.filter = filter;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }
}
