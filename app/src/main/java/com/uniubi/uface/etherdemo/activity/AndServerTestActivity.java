package com.uniubi.uface.etherdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.uniubi.andserver.EtherAndServerManager;
import com.uniubi.uface.etherdemo.R;
import com.uniubi.uface.ether.andserver.EtherAndServer;
import com.uniubi.uface.ether.utils.NetUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AndServerTestActivity extends AppCompatActivity {
    private static final String TAG = "AndServer";
    @BindView(R.id.text_ip)
    TextView textIp;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_and_server_test);
        ButterKnife.bind(this);
        textIp.setText(NetUtils.getLocalIPAddress()+"");

        EtherAndServerManager.getInstance().startAndServer(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
