package com.indilist.photoreceipt;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.indilist.photoreceipt.DTO.BoardFilterDTO;
import com.indilist.photoreceipt.DTO.BoardListDTO;

import java.util.List;

public class BoardFilterAdapter extends RecyclerView.Adapter<BoardFilterAdapter.MyViewHolder> {
    private Context context;
    private List<BoardFilterDTO> listItems;

    public BoardFilterAdapter(List<BoardFilterDTO> items, Context context) {
        this.listItems = items;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_filter_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        BoardFilterDTO item = listItems.get(holder.getAdapterPosition());
        holder.filterName.setText(item.getName());
        holder.filterValue.setText(item.getValue());
        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView filterName;
        private final TextView filterValue;

        private MyViewHolder(View itemView) {
            super(itemView);
            filterName = (TextView) itemView.findViewById(R.id.board_filter_item_name);
            filterValue = (TextView) itemView.findViewById(R.id.board_filter_item_value);
        }
    }
}
