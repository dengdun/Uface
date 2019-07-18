package com.whzxw.uface.ether.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.uniubi.uface.ether.BuildConfig;
import com.uniubi.uface.ether.R;
import com.whzxw.uface.ether.http.ApiService;
import com.whzxw.uface.ether.http.ResponseCabinetEntity;
import com.whzxw.uface.ether.http.RetrofitManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 显示柜子
 * 封装显示机柜控间。
 */
public class LockerView extends LinearLayout {
    /**
     * 初始化空数据，避免问题
     */
    private List<ResponseCabinetEntity.Cabinet> lockerList = new ArrayList<ResponseCabinetEntity.Cabinet>();
    public LockerView(Context context) {
        super(context);

        setOrientation(HORIZONTAL);
        init();
    }

    public LockerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        init();
    }

    public LockerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        init();
    }


    public LockerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(HORIZONTAL);
        init();
    }


    // 初始化数据并初始化最开显显示的空间。
    private void init()  {
        // 在调试情况下，在数组中添加调试数据，方便显示机柜。
        if (BuildConfig.DEBUG) {
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());

            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());

            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());

            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());
            lockerList.add(new ResponseCabinetEntity.Cabinet());

        }
    }

    public void setData() {
        // 列表的size 假设40个 每行显示10个
        int listSize = lockerList.size();
        int spanCount = 10;
        // 更改排列方式竖排 4排 强制
        int rowSize = 4;

        // 每次进来都清空子控件
        removeAllViews();
        for (int j = 1; j <= spanCount; j++) {
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            for (int i = 0; i < listSize; i++) {
                if (i < rowSize * j && i >= rowSize * (j - 1)) {
                    ResponseCabinetEntity.Cabinet cabinet = lockerList.get(i);

                    if (j % 2 == 1) {
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.locker_item, null);

                        ImageView v = ((ImageView) view.findViewById(R.id.item));
                        if ("@".equals(cabinet.getSno()) && cabinet.getUsed() == 1 && "1".equals(cabinet.getUsable())) {
                            v.setBackgroundColor(getResources().getColor(R.color.LockerColor));
                        } else if (cabinet.getUsed() == 1) {
                            v.setImageResource(R.drawable.locker_used);
                            v.setBackgroundColor(getResources().getColor(R.color.hadLockerColor));
                        } else {
                            v.setBackgroundColor(getResources().getColor(R.color.defaultLockerColor));
                        }

                        layout.addView(view);
                    } else {
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.locker_line_item, null);

                        ImageView v = ((ImageView) view.findViewById(R.id.item));
                        if ("@".equals(cabinet.getSno()) && cabinet.getUsed() == 1 && "1".equals(cabinet.getUsable())) {
                            v.setBackgroundColor(getResources().getColor(R.color.LockerColor));
                        } else if (cabinet.getUsed() == 1) {
                            v.setImageResource(R.drawable.locker_used);
                            v.setBackgroundColor(getResources().getColor(R.color.hadLockerColor));
                        } else {
                            v.setBackgroundColor(getResources().getColor(R.color.defaultLockerColor));
                        }

                        layout.addView(view);
                    }
                }
            }
            this.addView(layout);
        }
    }

    // 当每次调用打开机柜之后，通过调用该方法更新机柜的显示。
    public void refreshLocker() {
        RetrofitManager.getInstance()
                .apiService
                .queryCabinet(ApiService.queryCabinetUrl)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ResponseCabinetEntity>() {
                    @Override
                    public void accept(ResponseCabinetEntity listResponseEntity) throws Exception {
                        lockerList = listResponseEntity.getResult();
                        setData();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("error", "what error");
                    }
                });
    }


}
