package com.uniubi.uface.etherdemo.activity.outdevice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.uniubi.uface.ether.UfaceEtherOutDevice;
import com.uniubi.uface.etherdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OutDeviceActivity extends AppCompatActivity {

    @BindView(R.id.btn_id)
    Button btn_id;
    @BindView(R.id.btn_serial)
    Button btn_serial;
    @BindView(R.id.btn_open_close)
    Button btn_open_close;
    @BindView(R.id.btn_fp)
    Button btn_fp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_device);
        ButterKnife.bind(this);
        UfaceEtherOutDevice.init(this);
    }

    @OnClick({R.id.btn_id, R.id.btn_serial, R.id.btn_open_close, R.id.btn_fp, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_id:
                //身份证相关
                startActivity(new Intent(this, IDCardActivity.class));
                break;
            case R.id.btn_serial:
                //串口读取相关
                startActivity(new Intent(this, SerialPortActivity.class));
                break;
            case R.id.btn_open_close:
                //开关量相关
                startActivity(new Intent(this,OpenCloseActivity.class));
                break;
            case R.id.btn_fp:
                //指纹相关
                startActivity(new Intent(this,FpActivity.class));
                break;
            case R.id.btn_back:
                //返回上一层
                finish();
                break;
            default:
                break;
        }
    }
}
