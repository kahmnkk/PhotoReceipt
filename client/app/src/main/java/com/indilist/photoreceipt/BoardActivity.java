package com.indilist.photoreceipt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.indilist.photoreceipt.DTO.BoardFilterDTO;
import com.indilist.photoreceipt.DTO.BoardInfoDTO;
import com.indilist.photoreceipt.DTO.BoardLikeDTO;
import com.indilist.photoreceipt.DTO.BoardListDTO;
import com.indilist.photoreceipt.DTO.LoginDTO;
import com.indilist.photoreceipt.DTO.ResponseListDTO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardActivity extends AppCompatActivity {
    private final Gson gson = new Gson();

    private ImageView boardImg, iconLike;
    private TextView ownerText, likeCountText, boardText;
    private RecyclerView recyclerView;

    private Long boardIdx, userIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        SharedPreferences userInfo = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        userIdx = userInfo.getLong("USER_IDX", 0);

        Intent intent = getIntent();
        boardIdx = intent.getLongExtra("BOARD_IDX", 0);

        apiGetDetail();

        boardImg = (ImageView) findViewById(R.id.board_imageView);
        iconLike = (ImageView) findViewById(R.id.board_isLike);
        ownerText = (TextView) findViewById(R.id.board_owner);
        likeCountText = (TextView) findViewById(R.id.board_like_count);
        boardText = (TextView) findViewById(R.id.board_text);
        recyclerView = (RecyclerView) findViewById(R.id.filter_recyclerView);
    }

    private void apiGetDetail() {
        RetrofitClient retrofitClient = new RetrofitClient();

        JsonObject requestDTO = new JsonObject();
        requestDTO.addProperty("idx", boardIdx);
        requestDTO.addProperty("userIdx", userIdx);

        Call<JsonObject> call = retrofitClient.apiService.boardIndex("getDetail", requestDTO);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    ResponseManager responseManager = new ResponseManager(response.body());
                    if (responseManager.validResponse()) {
                        BoardInfoDTO boardInfoDTO = gson.fromJson(responseManager.getResult(), BoardInfoDTO.class);
                        updateUI(boardInfoDTO);
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

    private void updateUI(BoardInfoDTO boardInfoDTO) {
        BoardListDTO boardListDTO = gson.fromJson(boardInfoDTO.getBoardInfo(), BoardListDTO.class);
        LoginDTO loginDTO = gson.fromJson(boardInfoDTO.getOwnerInfo(), LoginDTO.class);

        Glide.with(this).load(boardListDTO.getImgLink()).error(R.drawable.ic_unlike).placeholder(R.drawable.ic_unlike).into(boardImg);
        if (boardInfoDTO.getIsLiked()) {
            Glide.with(this).load(R.drawable.ic_like).error(R.drawable.ic_unlike).placeholder(R.drawable.ic_unlike).into(iconLike);
        } else {
            Glide.with(this).load(R.drawable.ic_unlike).error(R.drawable.ic_unlike).placeholder(R.drawable.ic_unlike).into(iconLike);
        }

        ownerText.setText(loginDTO.getNickname());
        likeCountText.setText("좋아요 " + boardListDTO.getLike());
        boardText.setText(boardListDTO.getText());

        JsonObject filterObj = boardListDTO.getFilter();
        List<BoardFilterDTO> filterList = new ArrayList<>();
        Iterator iter = filterObj.keySet().iterator();
        while(iter.hasNext()) {
            String key = (String) iter.next();
            double value = filterObj.get(key).getAsDouble();
            String name = "";
            String tempValue = "";
            switch(key) {
                case "brightness":
                    value *= 0.02;
                    name = "밝기";
                    tempValue = value + "";
                    break;
                case "contrast":
                    value *= 0.02;
                    name = "대비";
                    tempValue = value + "";
                    break;
                case "saturation":
                    value *= 0.02;
                    name = "채도";
                    tempValue = value + "";
                    break;
                case "rboost":
                    value *= 0.02;
                    name = "R+";
                    tempValue = value + "";
                    break;
                case "gboost":
                    value *= 0.02;
                    name = "G+";
                    tempValue = value + "";
                    break;
                case "bboost":
                    value *= 0.02;
                    name = "B+";
                    tempValue = value + "";
                    break;
                case "exposure":
                    value = (value - 50) * 0.04;
                    name = "노출보정";
                    tempValue = value + "";
                    break;
                case "vignette":
                    value *= 0.01;
                    name = "비네팅";
                    tempValue = value + "";
                    break;
                case "grayscale":
                    name = "흑백";
                    if (value == 1) tempValue = "on";
                    else tempValue = "off";
                    break;
                case "sepia":
                    name = "세피아";
                    if (value == 1) tempValue = "on";
                    else tempValue = "off";
                    break;
                case "negative":
                    name = "네거티브";
                    if (value == 1) tempValue = "on";
                    else tempValue = "off";
                    break;
                case "hdr":
                    name = "hdr";
                    if (value == 1) tempValue = "on";
                    else tempValue = "off";
                    break;
            }

            BoardFilterDTO boardFilterDTO = new BoardFilterDTO();
            boardFilterDTO.setName(name);
            boardFilterDTO.setValue(tempValue);

            filterList.add(boardFilterDTO);
        }

        recyclerView.setAdapter(new BoardFilterAdapter(filterList, this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
    }

    public void onClickLike(View view) {
        apiLike();
    }

    private void apiLike() {
        RetrofitClient retrofitClient = new RetrofitClient();

        JsonObject requestDTO = new JsonObject();
        requestDTO.addProperty("idx", boardIdx);
        requestDTO.addProperty("userIdx", userIdx);

        Call<JsonObject> call = retrofitClient.apiService.boardIndex("like", requestDTO);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    ResponseManager responseManager = new ResponseManager(response.body());
                    if (responseManager.validResponse()) {
                        BoardLikeDTO boardLikeDTO = gson.fromJson(responseManager.getResult(), BoardLikeDTO.class);
                        updateLikeImg(boardLikeDTO);
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

    private void updateLikeImg(BoardLikeDTO boardLikeDTO) {
        if (boardLikeDTO.getIsLiked()) {
            Glide.with(this).load(R.drawable.ic_like).error(R.drawable.ic_unlike).placeholder(R.drawable.ic_unlike).into(iconLike);
        } else {
            Glide.with(this).load(R.drawable.ic_unlike).error(R.drawable.ic_unlike).placeholder(R.drawable.ic_unlike).into(iconLike);
        }

        likeCountText.setText("좋아요 " + boardLikeDTO.getLikeCount());
    }
}