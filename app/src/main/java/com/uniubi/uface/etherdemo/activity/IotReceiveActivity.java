package com.uniubi.uface.etherdemo.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uniubi.iot.iotbean.AsyncEvent;
import com.uniubi.uface.etherdemo.R;
import com.uniubi.uface.etherdemo.receiver.IotReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author qiaopeng
 * @date 2018/8/22
 */
public class IotReceiveActivity extends AppCompatActivity {

    @BindView(R.id.text_type)
    TextView textType;
    @BindView(R.id.text_msg)
    TextView textMsg;
    @BindView(R.id.ly_msg)
    LinearLayout lyMsg;

    private IotReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_iot);
        ButterKnife.bind(this);

        //实例化BroadcastReceiver子类 &  IntentFilter
        mBroadcastReceiver = new IotReceiver();
        IntentFilter intentFilter = new IntentFilter();

        //设置接收广播的类型
        intentFilter.addAction(AsyncEvent.IOT_MSG_ACTION);
        intentFilter.addAction(AsyncEvent.IOT_MSG_ACTION_EXTRA);

        //调用Context的registerReceiver（）方法进行动态注册
        registerReceiver(mBroadcastReceiver, intentFilter);

        mBroadcastReceiver.setOnMsgReceiveListener(new IotReceiver.OnMsgReceive() {
            @Override
            public void receiveMsg(int type, String msg) {
                textType.setText(type+"");
                if (!TextUtils.isEmpty(msg)){
                    textMsg.setText(msg);
                }else{
                    textMsg.setText(null);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mBroadcastReceiver);
    }
}
