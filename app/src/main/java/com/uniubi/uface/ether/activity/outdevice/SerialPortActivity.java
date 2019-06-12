package com.uniubi.uface.ether.activity.outdevice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uniubi.uface.etherdemo.R;
import com.uniubi.uface.ether.outdevice.serialport.EtherSerialPortManager;
import com.uniubi.uface.ether.utils.StringUtils;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SerialPortActivity extends AppCompatActivity {

    @BindView(R.id.btn_open)
    Button btn_open;
    @BindView(R.id.btn_close)
    Button btn_close;
    @BindView(R.id.btn_read)
    Button btn_read;
    @BindView(R.id.btn_write)
    Button btn_write;
    @BindView(R.id.btn_ic_card)
    Button btn_ic_card;
    @BindView(R.id.btn_code)
    Button btn_code;
    @BindView(R.id.btn_weigen)
    Button btn_weigen;
    //editText相关
    @BindView(R.id.et_open)
    EditText et_open;
    @BindView(R.id.et_write)
    EditText et_write;
    @BindView(R.id.et_weigen)
    EditText et_weigen;
    @BindView(R.id.et_baud)
    EditText et_baud;

    @BindView(R.id.tv_info)
    TextView tv_info;
    @BindView(R.id.btn_clear)
    Button mBtnClear;
    @BindView(R.id.btn_back)
    Button mBtnBack;
    private int fd;
    private static final String TAG = "SerialPort";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_open, R.id.btn_close, R.id.btn_read, R.id.btn_write, R.id.btn_ic_card, R.id.btn_code, R.id.btn_weigen, R.id.btn_clear, R.id.btn_back, R.id.et_open})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_open:
                //打开串口
                String content = et_open.getText().toString().trim();
                if (!TextUtils.isEmpty(et_baud.getText())&&!TextUtils.isEmpty(content)){
                    int baud = Integer.valueOf(et_baud.getText().toString().trim());
                    fd = EtherSerialPortManager.getInstance().openSerialPort(content, baud);
                    if (fd>0){
                        Toast.makeText(SerialPortActivity.this,"打开成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SerialPortActivity.this,"打开失败",Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.btn_close:
                //关闭串口
                int fb = EtherSerialPortManager.getInstance().closeSerialPort(fd);
                if (fb>=0){
                    Toast.makeText(SerialPortActivity.this,"关闭成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SerialPortActivity.this,"关闭失败",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_read:
                //读取串口
                char[] chars = new char[20];
                int length = EtherSerialPortManager.getInstance().readSerialPort(fd, chars);
                if (chars != null) {
                    tv_info.setText(Arrays.toString(chars) + "==" + length);
                }
                break;
            case R.id.btn_write:
                //串口写入
                String writeSp = et_write.getText().toString().trim();
                if (!TextUtils.isEmpty(writeSp)) {
                    char[] aaa = writeSp.toCharArray();
                    EtherSerialPortManager.getInstance().writeSerialPort(fd, aaa);
                    tv_info.setText(Arrays.toString(aaa));
                }

                break;
            case R.id.btn_ic_card:
                //ic卡读取
                String idCard = EtherSerialPortManager.getInstance().readICardNum(fd,2048,EtherSerialPortManager.GPIO_UFACE2);
                if (!TextUtils.isEmpty(idCard)) {
                    tv_info.setText(idCard);
                }
                break;
            case R.id.btn_code:
                //二维码读取
                String code = EtherSerialPortManager.getInstance().readQRCode(fd);
                if (!TextUtils.isEmpty(code)) {
                    tv_info.setText(code);
                    ;
                }
                break;
            case R.id.btn_weigen:
                //韦根读取
                String writeContent = et_write.getText().toString().trim();
                String path = et_weigen.getText().toString().trim();
                int mode=1;
                if (!TextUtils.isEmpty(path)) {
                    mode = Integer.parseInt(path);
                }
                if (!TextUtils.isEmpty(writeContent)) {
                    try {
                        byte[] bytes = StringUtils.LongToBytes(Long.parseLong(writeContent));
                        EtherSerialPortManager.getInstance().writeWiegand(bytes, mode);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(SerialPortActivity.this,"韦根写入内容请输入数字字符串",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.btn_clear:
                //清除信息
                tv_info.setText("");
                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }

}
