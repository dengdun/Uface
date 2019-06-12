package com.uniubi.uface.ether.serverhandle;

import com.uniubi.uface.ether.andserver.handler.AbstractEtherRequestHandler;
import com.uniubi.uface.ether.bean.StartAppEvent;
import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.annotation.RequestMapping;

import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.protocol.HttpContext;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

/**
 * 启动识别界面
 */
public class StartRecoHandler extends AbstractEtherRequestHandler {
    public StartRecoHandler(String url) {
        super(url);
    }

    @RequestMapping(method = {RequestMethod.GET})
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        // 打开识别界面
        EventBus.getDefault().post(new StartAppEvent());
    }
}
