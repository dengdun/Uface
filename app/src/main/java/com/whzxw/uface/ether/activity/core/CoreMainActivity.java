package com.whzxw.uface.ether.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.uniubi.uface.ether.R;
import com.uniubi.uface.ether.base.UfaceEtherImpl;
import com.uniubi.uface.ether.config.AlgorithmOptions;
import com.uniubi.uface.ether.config.CommonOptions;
import com.uniubi.uface.ether.config.ServiceOptions;
import com.uniubi.uface.ether.config.configenum.service.RecoMode;
import com.uniubi.uface.ether.config.configenum.service.RecoPattern;
import com.uniubi.uface.ether.config.configenum.service.WorkMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author qiaopeng
 * @date 2018/8/15
 */
public class CoreMainActivity extends AppCompatActivity {

    @BindView(R.id.radioButton)
    RadioButton radioButton;
    @BindView(R.id.radioButton2)
    RadioButton radioButton2;
    @BindView(R.id.radioButton3)
    RadioButton radioButton3;
    @BindView(R.id.radioButton4)
    RadioButton radioButton4;
    @BindView(R.id.recmode)
    RadioGroup recmode;
    @BindView(R.id.radioButton5)
    RadioButton radioButton5;
    @BindView(R.id.radioButton6)
    RadioButton radioButton6;
    @BindView(R.id.recpatern)
    RadioGroup recpatern;
    @BindView(R.id.btn_core_single)
    Button btnCoreSingle;
    @BindView(R.id.btn_core_continue)
    Button btnCoreContinue;


    private ServiceOptions options;
    private AlgorithmOptions algorithmOptions;
    private CommonOptions commonOptions;
    private int deviceType = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        options = UfaceEtherImpl.getServiceOptions();
        algorithmOptions = UfaceEtherImpl.getAlgorithmOptions();
        commonOptions = UfaceEtherImpl.getCommonOptions();
        setContentView(R.layout.activity_coremain);
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        radioButton5.setChecked(true);
        radioButton.setChecked(true);
        options.setRecoMode(RecoMode.LOCALONLY);
        options.setRecoPattern(RecoPattern.VERIFY);
        if (options.getWorkMode()== WorkMode.OFFLINE){

            radioButton2.setVisibility(View.GONE);
            radioButton3.setVisibility(View.GONE);
            radioButton4.setVisibility(View.GONE);

            recmode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    options.setRecoPattern(RecoPattern.IDENTIFY);
                    radioButton6.setChecked(true);
                }
            });
            recpatern.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.radioButton5) {
                        options.setRecoPattern(RecoPattern.VERIFY);
                    } else {
                        options.setRecoPattern(RecoPattern.IDENTIFY);
                    }
                }
            });

        }else{
            recmode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.radioButton:
                            options.setRecoMode(RecoMode.LOCALONLY);
                            break;
                        case R.id.radioButton2:
                            options.setRecoMode(RecoMode.LOCALFIRST);
                            break;
                        case R.id.radioButton3:
                            options.setRecoMode(RecoMode.NETONLY);
                            break;
                        case R.id.radioButton4:
                            options.setRecoMode(RecoMode.NETFIRST);
                            break;
                        default:
                            break;
                    }

                    if (checkedId != R.id.radioButton) {
                        options.setRecoPattern(RecoPattern.IDENTIFY);
                        radioButton6.setChecked(true);
                    }
                }
            });
            recpatern.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.radioButton5) {
                        if (!radioButton.isChecked()) {
                            radioButton5.setChecked(false);
                            radioButton6.setChecked(true);
                        } else {
                            options.setRecoPattern(RecoPattern.VERIFY);
                        }
                    } else {
                        options.setRecoPattern(RecoPattern.IDENTIFY);
                    }
                }
            });
        }



    }


    @OnClick({R.id.btn_core_single, R.id.btn_core_continue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_core_continue:
                Intent intent1 = new Intent(CoreMainActivity.this, CoreRecoActivity.class);
                intent1.putExtra("deviceType", deviceType);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }
}
