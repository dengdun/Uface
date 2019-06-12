package com.uniubi.uface.ether.adapter;


import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class LockerAdapter extends RecyclerView.Adapter {
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setBackgroundColor(Color.BLUE);

        return new LockerHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position%3 == 0) {
            holder.itemView.setBackgroundColor(Color.BLUE);
        } else if (position %3 == 1) {
            holder.itemView.setBackgroundColor(Color.RED);
        } else if (position %3 == 2) {
            holder.itemView.setBackgroundColor(Color.YELLOW);
        }

    }

    @Override
    public int getItemCount() {
        return 30;
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
}
