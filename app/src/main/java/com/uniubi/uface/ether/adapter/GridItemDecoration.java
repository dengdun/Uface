package com.uniubi.uface.ether.adapter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 表格增加表格线
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private final ColorDrawable mDriver;

    public GridItemDecoration() {
        mDriver = new ColorDrawable(Color. BLACK);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        // 第一行第二行要绘制
        for (int i = 0; i < parent.getChildCount(); i ++) {

            // 这里正常的顺序是1 3579 奇数列右边划线
            // 程序员是从0开始数的
            // 先计算列号
            int coloum = i+1 % 10;
            if ((coloum )%2 == 1) {
                View childView = parent.getChildAt(i);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) childView.getLayoutParams();

                mDriver.setBounds(childView.getRight() , childView.getTop()- layoutParams.topMargin, childView.getRight()+  layoutParams.rightMargin, childView.getBottom() + layoutParams.bottomMargin);

                mDriver.draw(c);
            }
         }
    }
}
