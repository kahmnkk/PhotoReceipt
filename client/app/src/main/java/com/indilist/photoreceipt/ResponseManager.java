package com.indilist.photoreceipt;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.indilist.photoreceipt.DTO.ResponseDataDTO;
import com.indilist.photoreceipt.DTO.ResponseErrorDTO;
import com.indilist.photoreceipt.DTO.ResponseResultDTO;

public class ResponseManager {
    private final Gson gson = new Gson();
    private final JsonObject responseData;

    ResponseManager(JsonObject responseData) {
        this.responseData = responseData;
    }

    public Boolean validResponse() {
        if (responseData.get("error") != null) {
            return false;
        } else {
            return true;
        }
    }

    public JsonObject getResult() {
        ResponseDataDTO data = gson.fromJson(responseData, ResponseDataDTO.class);
        ResponseResultDTO rtn = gson.fromJson(data.getData(), ResponseResultDTO.class);
        return rtn.getResult();
    }

    public void errorHandler(Context context) {
        String msg = "undefined error";
        ResponseErrorDTO errorDTO = gson.fromJson(responseData.get("error"), ResponseErrorDTO.class);

        switch (errorDTO.getCode()) {
            case 11001:
            case 11002:
                msg = context.getString(R.string.err_invalid_account);
                break;

            case 11003:
                msg = context.getString(R.string.err_duplicate_account);
                break;

        }

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
