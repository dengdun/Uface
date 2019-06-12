package com.uniubi.uface.ether.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.uniubi.uface.etherdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author qiaopeng
 * @date 2018/8/18
 */
public class CoreNaviActivity extends AppCompatActivity {

    @BindView(R.id.btn_interface)
    Button btnInterface;
    @BindView(R.id.btn_face)
    Button btnFace;
    @BindView(R.id.btn_net)
    Button btnNet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corenavi);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_interface, R.id.btn_face, R.id.btn_net})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_interface:
                intent = new Intent(CoreNaviActivity.this, CoreInterfaceActivity.class);
                break;
            case R.id.btn_face:
                intent = new Intent(CoreNaviActivity.this, CoreMainActivity.class);
                break;
            case R.id.btn_net:
//                intent = new Intent(CoreNaviActivity.this, MainActivity.class);
                break;
            default:
                break;

        }
        startActivity(intent);
    }
}
