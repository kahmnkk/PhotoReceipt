package com.indilist.photoreceipt.ui.camera;

import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indilist.photoreceipt.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.filterViewHolder>{
    ArrayList<PresetFilter> list;

    public void updateList(ArrayList<PresetFilter> newlist){
        this.list = newlist;
        notifyDataSetChanged();
    }
    public interface itemsClickListener
    {
        void nameOnClick(View v, int position);
        void editOnClick(View v, int position);
        void delOnClick(View v, int position);
    }
    public itemsClickListener onClickListener;
    public class filterViewHolder extends RecyclerView.ViewHolder {
        private TextView filterNameView;
        private ImageButton editBtn;
        private ImageButton delBtn;


        public filterViewHolder(@NonNull View itemView) {
            super(itemView);
            filterNameView = (TextView)itemView.findViewById(R.id.filter_name);
            editBtn = (ImageButton)itemView.findViewById(R.id.edit_filter_btn);
            delBtn = (ImageButton)itemView.findViewById(R.id.delete_filter_btn);
            editBtn.setBackgroundColor(Color.TRANSPARENT);
            delBtn.setBackgroundColor(Color.TRANSPARENT);

            filterNameView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    onClickListener.nameOnClick(view, getAdapterPosition());
                }
            });
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.editOnClick(view, getAdapterPosition());
                }
            });
            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.delOnClick(view, getAdapterPosition());
                }
            });
        }
        public TextView getFilterNameView(){
            return filterNameView;
        }
        public ImageButton getEditBtn(){
            return editBtn;
        }
        public ImageButton getDelBtn(){
            return delBtn;
        }
    }

    public FilterListAdapter(ArrayList<PresetFilter> list, itemsClickListener listener){
        this.list = list;
        this.onClickListener = listener;
    }


    @NonNull
    @Override
    public FilterListAdapter.filterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_list_item, parent, false);
        return new filterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull filterViewHolder holder, int position) {
        holder.getFilterNameView().setText(list.get(position).getFilterName());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


}

