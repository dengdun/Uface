package com.whzxw.uface.ether.adapter;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uniubi.uface.ether.R;
import com.whzxw.uface.ether.http.ResponseCabinetEntity;

import java.util.List;

public class LockerAdapter extends RecyclerView.Adapter {
    public static final int LINE = 0x0001;
    public static final int NO_LINE = 0x0002;
    List<ResponseCabinetEntity.Cabinet> list;
    public LockerAdapter(List<ResponseCabinetEntity.Cabinet> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(54, 54);
        layoutParams.setMargins(1, 2, 1, 2);
        imageView.setLayoutParams(layoutParams);
        imageView.setBackgroundColor(Color.BLUE);

        return new LockerHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        ImageView itemView = (ImageView) holder.itemView;
        // used为0表示未存放，1表示已存放；
        if (list.get(position).getUsed() == 0) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.defaultLockerColor));
        } else if (list.get(position).getUsed() == 1) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.hadLockerColor));
            itemView.setImageResource(R.drawable.lease);
            itemView.setPadding(6, 6, 6, 6);
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.LockerColor));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public int getItemViewType(int position) {
        if ((position+1 % 10)%2 == 1) {
            return LINE;
        }
        return NO_LINE;
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

    public void setList(List<ResponseCabinetEntity.Cabinet> list) {
        this.list = list;
    }
}
