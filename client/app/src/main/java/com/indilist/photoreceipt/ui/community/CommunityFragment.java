package com.indilist.photoreceipt.ui.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.indilist.photoreceipt.BoardAdapter;
import com.indilist.photoreceipt.DTO.BoardListDTO;
import com.indilist.photoreceipt.DTO.LoginDTO;
import com.indilist.photoreceipt.DTO.ResponseListDTO;
import com.indilist.photoreceipt.JoinActivity;
import com.indilist.photoreceipt.LoginActivity;
import com.indilist.photoreceipt.MainActivity;
import com.indilist.photoreceipt.R;
import com.indilist.photoreceipt.ResponseManager;
import com.indilist.photoreceipt.RetrofitClient;
import com.indilist.photoreceipt.UploadActivity;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityFragment extends Fragment {
    private final Gson gson = new Gson();

    private CommunityViewModel communityViewModel;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        communityViewModel =
                new ViewModelProvider(this).get(CommunityViewModel.class);
        View view = inflater.inflate(R.layout.fragment_community, container, false);
//        final TextView textView = root.findViewById(R.id.text_notifications);
//        communityViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        apiGetList();

        return view;
    }

    public void apiGetList() {
        RetrofitClient retrofitClient = new RetrofitClient();

        JsonObject requestDTO = new JsonObject();

        Call<JsonObject> call = retrofitClient.apiService.boardIndex("getList", requestDTO);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    ResponseManager responseManager = new ResponseManager(response.body());
                    if (responseManager.validResponse()) {
                        ResponseListDTO listDTO = gson.fromJson(responseManager.getResult(), ResponseListDTO.class);
                        Type collectionType = new TypeToken<List<BoardListDTO>>() {}.getType();
                        List<BoardListDTO> dataList = gson.fromJson(listDTO.getList(), collectionType);

                        recyclerView.setAdapter(new BoardAdapter(dataList, getActivity()));
                        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                    } else {
                        responseManager.errorHandler(getActivity().getApplicationContext());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}