package com.uniubi.uface.etherdemo.activity.outdevice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uniubi.uface.etherdemo.R;
import com.uniubi.uface.ether.outdevice.utils.ZKTFpOperator;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 指纹读取界面
 */
public class FpActivity extends AppCompatActivity implements ZKTFpOperator.ZKTFpCallBack {

    @BindView(R.id.btn_start)
    Button mBtnStart;
    @BindView(R.id.btn_end)
    Button mBtnEnd;
    @BindView(R.id.tv_info)
    TextView mTvInfo;
    @BindView(R.id.btn_clear)
    Button mBtnClear;
    @BindView(R.id.btn_back)
    Button mBtnBack;
    //测试模板，二代证采集的1024字节有2个模板
    private byte[] mTestFeature = new byte[512];
    //测试模板Base64字符串
    private String mstrTemplate = "QwH/EgFjSAAAAAAAAAAAAAAAACwBmmq0AP///////7QjTP5ZKRb8miux/JE4Yv5LP+L8RE8q/IJTV/7BV0H+cWIG/LtqlPxpcBT8i4FD/oCQO/45mS38d586/qGhKf5fsuv8arfv/JO3cPw2uD78b78B/qTGGf6SzBT+T9E8/GvR8/wo4j78b/Pu/K/0Ef4lCzD9oxEA/70WH/+MG+79ZB7i/a0hA/8sJSz9PCsq/VQ2Lv2+Pnj9jEI3/alFH/1QUhj9qlIA/YtXJv1pXCD9AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACpAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJc=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fp);
        ButterKnife.bind(this);
        mTestFeature = Base64.decode(mstrTemplate, Base64.NO_WRAP);
    }

    @OnClick({R.id.btn_start, R.id.btn_end, R.id.btn_clear, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                //开始读取指纹
                ZKTFpOperator.getInstance().getFpInfo(FpActivity.this);
                break;
            case R.id.btn_end:
                //关闭读取指纹
                ZKTFpOperator.getInstance().closeFp();
                break;
            case R.id.btn_clear:
                mTvInfo.setText("");
                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void getFpData(final byte[] bytes) {
        if (bytes != null) {
            //读取到的指纹信息
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int result = ZKTFpOperator.getInstance().getFpResult(bytes, mTestFeature);
                    mTvInfo.setText("比对结果："+result+"=="+"读取到的指纹信息:" + Arrays.toString(bytes));
                }
            });
        }
    }

    @Override
    public void errorMsg(String msg) {
        Toast.makeText(FpActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        ZKTFpOperator.getInstance().closeFp();
        super.onPause();
    }
}
