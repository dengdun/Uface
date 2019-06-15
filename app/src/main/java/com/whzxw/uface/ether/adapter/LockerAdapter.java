package com.whzxw.uface.ether.adapter;


import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.whzxw.uface.ether.http.CabinetBean;

import java.util.List;

public class LockerAdapter extends RecyclerView.Adapter {
    List<CabinetBean> list;
    public LockerAdapter(List<CabinetBean> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
        layoutParams.setMargins(10, 10, 10, 10);
        imageView.setLayoutParams(layoutParams);
        imageView.setBackgroundColor(Color.BLUE);

        return new LockerHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // used为0表示未存放，1表示已存放；
        if (list.get(position).getUsed() == 0) {
            holder.itemView.setBackgroundColor(Color.GREEN);
        } else if (list.get(position).getUsed() == 1) {
            holder.itemView.setBackgroundColor(Color.RED);
        } else {
            holder.itemView.setBackgroundColor(Color.DKGRAY);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class LockerHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        public LockerHolder(View itemView) {
            super(itemView);
            imageView = ((ImageView) itemView);
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    public void setList(List<CabinetBean> list) {
        this.list = list;
    }
}
