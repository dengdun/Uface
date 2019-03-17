package com.uniubi.uface.etherdemo.serverhandle;

import com.uniubi.uface.ether.andserver.handler.AbstractEtherRequestHandler;
import com.uniubi.uface.etherdemo.bean.ScreenSaverMessageEvent;
import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Map;

public class ScreenHandler extends AbstractEtherRequestHandler {

    public ScreenHandler(String url) {
        super(url);
    }

    @RequestMapping(method = {RequestMethod.POST})
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        Map<String, String> params = HttpRequestParser.parseParams(request);
        if (!params.containsKey("screenSaver")) {
            // 这里是屏幕保护的字段
            return;
        }
        boolean screenSaver = Boolean.parseBoolean(params.get("screenSaver"));
        EventBus.getDefault().post(new ScreenSaverMessageEvent(screenSaver));
        response(response, "{\"success\":\"true\"}");
    }

    private void response(HttpResponse response, String info) {
        StringEntity stringEntity = new StringEntity(info, "utf-8");
        response.setStatusCode(200);
        response.setEntity(stringEntity);
    }
}
