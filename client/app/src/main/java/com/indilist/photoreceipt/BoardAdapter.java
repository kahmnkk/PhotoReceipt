package com.indilist.photoreceipt;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.indilist.photoreceipt.DTO.BoardListDTO;

import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.MyViewHolder> implements ItemClickListener {
    private Context context;
    private List<BoardListDTO> listItems;

    public BoardAdapter(List<BoardListDTO> items, Context context) {
        this.listItems = items;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        BoardListDTO item = listItems.get(holder.getAdapterPosition());
        Glide.with(context).load(item.getImgLink()).error(R.drawable.image_gallery).placeholder(R.drawable.image_gallery).into(holder.imgAlbumArt);
        holder.itemView.setTag(item);
    }

    @Override
    public void onItemClick(int position) {
        BoardListDTO item = listItems.get(position);
        Intent intent = new Intent(context.getApplicationContext(), BoardActivity.class);
        intent.putExtra("BOARD_IDX", item.getIdx());
        context.getApplicationContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAlbumArt;

        private MyViewHolder(View itemView, final ItemClickListener itemClickListener) {
            super(itemView);
            imgAlbumArt = (ImageView) itemView.findViewById(R.id.list_item_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
