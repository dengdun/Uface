package com.whzxw.uface.ether.adapter;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.uniubi.uface.ether.R;
import com.whzxw.uface.ether.EtherApp;

/**
 * 表格增加表格线
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private final ColorDrawable mDriver;

    public GridItemDecoration() {

        mDriver = new ColorDrawable(EtherApp.context.getResources().getColor(R.color.lockerline));
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        // 第一行第二行要绘制
//        for (int i = 0; i < parent.getChildCount(); i ++) {
//
//            // 这里正常的顺序是1 3579 奇数列右边划线
//            // 程序员是从0开始数的
//            // 先计算列号
//            int coloum = i+1 % 10;
//            if ((coloum )%2 == 1) {
//                View leftchildView = parent.getChildAt(i);
//                RecyclerView.LayoutParams leftchildViewLayoutParams = (RecyclerView.LayoutParams) leftchildView.getLayoutParams();
//                int moveDisatance = 0;
//                mDriver.setBounds(leftchildView.getRight() + moveDisatance, leftchildView.getTop() - leftchildViewLayoutParams.topMargin , leftchildView.getRight() +  2*leftchildViewLayoutParams.rightMargin - moveDisatance, leftchildView.getBottom() + leftchildViewLayoutParams.bottomMargin);
//                mDriver.draw(c);
//            }
//         }
    }

//    @Override
//    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        super.getItemOffsets(outRect, view, parent, state);
//        int childAdapterPosition = parent.getChildAdapterPosition(view);
////        int coloum = childAdapterPosition+1 % 10;
//
////        outRect.left = 0;
////        outRect.top = 0;
////        outRect.bottom = 0;
//
//        // Add top margin only for the first item to avoid double space between items
//        if (childAdapterPosition%3 == 1) {
////            outRect.right = 20;
////            outRect.top = 5;
//            outRect.set(0, 0, -20, 0);
//        } else {
//            outRect.set(0, 0, 0, 0);
////            outRect.right = 3;
////            outRect.top = 0;
//        }
//    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spanCount  = 10;
        int spacing  = 3;
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        if (true) {
            // 分割线
            outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom


        } else {
            outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom

        }
    }
}
