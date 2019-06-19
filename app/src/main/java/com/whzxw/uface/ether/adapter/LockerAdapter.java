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
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.locker_item, parent, false);

//        final ImageView imageView = new ImageView(parent.getContext());
//        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(30, 30);
//        marginLayoutParams.height = 30;
//        marginLayoutParams.width = 30;
//        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                final int type = viewType;
//                final ViewGroup.LayoutParams lp = imageView.getLayoutParams();
//                if (lp instanceof GridLayoutManager.LayoutParams) {
//                    GridLayoutManager.LayoutParams sglp = (GridLayoutManager.LayoutParams) lp;
//                    int viewLayoutPosition = sglp.getViewLayoutPosition();
//
//                    switch (type) {
//                        case LINE:
//                            sglp.width = 30;
//                            sglp.height = 30;
//                            break;
//                        case NO_LINE:
//                            sglp.width = 30;
//                            sglp.height = 30;
//                            break;
//                    }
//                    imageView.setLayoutParams(sglp);
//                }
//                final GridLayoutManager lm =
//                        (GridLayoutManager) ((RecyclerView) parent).getLayoutManager();
//
//                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
//                return true;
//            }
//        });
//        GridLayoutManager.LayoutParams sglp = (GridLayoutManager.LayoutParams) imageView.getLayoutParams();
//        int viewLayoutPosition = sglp.getViewLayoutPosition();
//        sglp.width = 30;
//        sglp.height = 30;
//        switch (viewType) {
//            case LINE:
//                sglp.width = 30;
//                sglp.height = 30;
//                break;
//            case NO_LINE:
//                sglp.width = 30;
//                sglp.height = 30;
//                break;
//        }
//        imageView.setLayoutParams(marginLayoutParams);
        return new LockerHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        ImageView itemView = (ImageView) holder.itemView;
        // used为0表示未存放，1表示已存放；
        if (list.get(position).getUsed() == 0) {
//            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.defaultLockerColor));
        } else if (list.get(position).getUsed() == 1) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.hadLockerColor));
            itemView.setImageResource(R.drawable.lease);
            itemView.setPadding(10, 10, 10, 10);
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
        if (position% 3 == 1)
            return LINE;
        else
            return NO_LINE;
    }

    class LockerHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final ImageView linear;
        public LockerHolder(View itemView) {
            super(itemView);
            imageView = ((ImageView)  itemView.findViewById(R.id.box));
            linear = ((ImageView)  itemView.findViewById(R.id.liner));
        }

    }

    public void setList(List<ResponseCabinetEntity.Cabinet> list) {
        this.list = list;
    }

    class LockerLineHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        public LockerLineHolder(View itemView) {
            super(itemView);
            imageView = ((ImageView)  itemView);
        }

        public ImageView getImageView() {
            return imageView;
        }
    }
}
