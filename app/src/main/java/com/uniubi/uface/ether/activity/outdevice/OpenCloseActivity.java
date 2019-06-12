package com.uniubi.uface.ether.activity.outdevice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.uniubi.uface.etherdemo.R;
import com.uniubi.uface.ether.outdevice.utils.FileNodeOperator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OpenCloseActivity extends AppCompatActivity {

    @BindView(R.id.btn_open_1)
    Button btn_open_1;
    @BindView(R.id.btn_open_2)
    Button btn_open_2;
    @BindView(R.id.btn_close_1)
    Button btn_close_1;
    @BindView(R.id.btn_close_2)
    Button btn_close_2;
    @BindView(R.id.btn_back)
    Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_close);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_open_1, R.id.btn_open_2, R.id.btn_close_1, R.id.btn_close_2, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_open_1:
                //打开继电器
                FileNodeOperator.open(FileNodeOperator.RELAY_PATH);
                break;
            case R.id.btn_open_2:
                //打开补光灯
                FileNodeOperator.open(FileNodeOperator.LED_PATH);
                break;
            case R.id.btn_close_1:
                //关闭继电器
                FileNodeOperator.close(FileNodeOperator.RELAY_PATH);
                break;
            case R.id.btn_close_2:
                //关闭补光灯
                FileNodeOperator.close(FileNodeOperator.LED_PATH);
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }
}
