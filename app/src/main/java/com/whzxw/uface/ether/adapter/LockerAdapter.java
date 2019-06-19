package com.whzxw.uface.ether.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        switch (viewType) {
            case LINE:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.locker_line_item, parent, false);
                return  new LockerLineHolder(view1);

            default:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.locker_item, parent, false);
                return  new LockerHolder(view2);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Context context = holder.itemView.getContext();
        int itemViewType = holder.getItemViewType();
        switch (itemViewType) {
            case LINE:
                LockerLineHolder lockerLineHolder = (LockerLineHolder) holder;
                lockerLineHolder.linear.setBackgroundColor(context.getResources().getColor(R.color.lockerline));
                if (list.get(position).getUsed() == 0) {
                    holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.defaultLockerColor));
                } else if (list.get(position).getUsed() == 1) {
                    lockerLineHolder.imageView.setBackgroundColor(context.getResources().getColor(R.color.hadLockerColor));
                    lockerLineHolder.imageView.setImageResource(R.drawable.lease);
                    lockerLineHolder.imageView.setPadding(10, 10, 10, 10);
                } else {
                    lockerLineHolder.imageView.setBackgroundColor(context.getResources().getColor(R.color.LockerColor));
                }
                break;
            default:
                LockerHolder lockerHolder = (LockerHolder) holder;
                // used为0表示未存放，1表示已存放；
                if (list.get(position).getUsed() == 0) {
                    holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.defaultLockerColor));
                } else if (list.get(position).getUsed() == 1) {
                    lockerHolder.imageView.setBackgroundColor(context.getResources().getColor(R.color.hadLockerColor));
                    lockerHolder.imageView.setImageResource(R.drawable.lease);
                    lockerHolder.imageView.setPadding(10, 10, 10, 10);
                } else {
                    lockerHolder.imageView.setBackgroundColor(context.getResources().getColor(R.color.LockerColor));
                }
        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (position%2 == 0)
            return LINE;
        else
            return NO_LINE;
    }

    class LockerHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        public LockerHolder(View itemView) {
            super(itemView);
            imageView = ((ImageView)  itemView.findViewById(R.id.box));
        }

    }

    class LockerLineHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final ImageView linear;
        public LockerLineHolder(View itemView) {
            super(itemView);
            imageView = ((ImageView)  itemView.findViewById(R.id.box));
            linear = ((ImageView)  itemView.findViewById(R.id.liner));
        }

    }

    public void setList(List<ResponseCabinetEntity.Cabinet> list) {
        this.list = list;
    }


}
