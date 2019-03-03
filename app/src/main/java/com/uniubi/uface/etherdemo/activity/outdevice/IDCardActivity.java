package com.uniubi.uface.etherdemo.activity.outdevice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uniubi.uface.etherdemo.R;
import com.uniubi.uface.ether.outdevice.bean.CardInfo;
import com.uniubi.uface.ether.outdevice.utils.EtherIDCardManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IDCardActivity extends AppCompatActivity {

    @BindView(R.id.btn_serialport)
    Button btn_serialport;
    @BindView(R.id.btn_jl)
    Button btn_jl;
    @BindView(R.id.btn_zkt)
    Button btn_zkt;
    @BindView(R.id.btn_jl_release)
    Button btn_jl_release;
    @BindView(R.id.btn_zkt_release)
    Button btn_zkt_release;
    @BindView(R.id.btn_back)
    Button btn_back;
    @BindView(R.id.btn_clear)
    Button btn_clear;
    @BindView(R.id.tv_info)
    TextView tv_info;
    @BindView(R.id.iv)
    ImageView iv;
    private static final String TAG = "IDCard";
    private ExecutorService threadPool;
    private boolean isReading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_card);
        ButterKnife.bind(this);
        threadPool = Executors.newFixedThreadPool(1);
    }

    @OnClick({R.id.btn_serialport, R.id.btn_jl, R.id.btn_zkt, R.id.btn_jl_release, R.id.btn_zkt_release, R.id.btn_back, R.id.tv_info, R.id.iv, R.id.btn_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_serialport:
                //串口读取身份证
                if (isReading){
                    return;
                }
                isReading=true;
                threadPool.execute(new ReadCardRunnable());
                break;
            case R.id.btn_jl:
                //精伦usb读取身份证
                CardInfo jlInfo = EtherIDCardManager.usbReadIDCard(1,false);
                if (jlInfo != null) {
                    Log.d(TAG, jlInfo.toString());
                    tv_info.setText(jlInfo.toString());
                    iv.setImageBitmap(jlInfo.getPhoto());
                }
                break;
            case R.id.btn_zkt:
                //中控等usb读取身份证
                CardInfo zktInfo = EtherIDCardManager.usbReadIDCard(2,false);
                if (zktInfo != null) {
                    Log.d(TAG, zktInfo.toString());
                    tv_info.setText(zktInfo.toString());
                    iv.setImageBitmap(zktInfo.getPhoto());
                }
                break;
            case R.id.btn_jl_release:
                //精伦usb释放资源
                EtherIDCardManager.releaseUsb(1);
                break;
            case R.id.btn_zkt_release:
                //中控等usb释放资源
                EtherIDCardManager.releaseUsb(2);
                break;
            case R.id.tv_info:
                break;
            case R.id.iv:
                break;
            case R.id.btn_clear:
                //清除信息
                tv_info.setText("");
                iv.setImageBitmap(null);
                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;

        }
    }

    private class ReadCardRunnable implements Runnable {
        @Override
        public void run() {
            final CardInfo cardInfo = EtherIDCardManager.serialPortReadIDCard("/dev/ttyS1");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (cardInfo != null) {
                        Log.d(TAG, cardInfo.toString());
                        tv_info.setText(cardInfo.toString());
                        iv.setImageBitmap(cardInfo.getPhoto());
                    }else {
                        Toast.makeText(IDCardActivity.this,"读取失败，请稍候重试",Toast.LENGTH_SHORT).show();
                    }
                    isReading=false;
                }
            });
        }
    }
}
