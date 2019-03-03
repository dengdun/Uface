package com.uniubi.uface.etherdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.uniubi.iot.iotbean.AsyncEvent;
import com.uniubi.uface.ether.utils.AppLog;

/**
 * @author qiaopeng
 * @date 2018/8/22
 */
public class IotReceiver extends BroadcastReceiver{
    OnMsgReceive onMsgReceive;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(AsyncEvent.IOT_MSG_ACTION)) {
            //Ether广播返回的AsyncEvent对象
            AsyncEvent event = intent.getParcelableExtra(AsyncEvent.IOT_MSG_ACTION_EXTRA);
            //消息类型
            int type = event.getType();
            //自定义消息内容
            String msg = event.getMsg();

            AppLog.e("type ---> " + type);
            if (msg!=null) {
                AppLog.e("msg ---> " + msg);
            }
            if (onMsgReceive!=null){
                onMsgReceive.receiveMsg(type,msg);
            }
        }
    }

    public interface OnMsgReceive{
        void receiveMsg(int type, String msg);
    }

    public void setOnMsgReceiveListener(OnMsgReceive onMsgReceive){
        this.onMsgReceive = onMsgReceive;

    }
}
