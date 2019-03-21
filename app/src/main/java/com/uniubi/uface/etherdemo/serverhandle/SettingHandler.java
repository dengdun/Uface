package com.uniubi.uface.etherdemo.serverhandle;

import com.uniubi.uface.ether.andserver.handler.AbstractEtherRequestHandler;
import com.uniubi.uface.etherdemo.EtherApp;
import com.uniubi.uface.etherdemo.bean.SettingMessageEvent;
import com.uniubi.uface.etherdemo.utils.ShareUtils;
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

/**
 * 设置页面的
 */
public class SettingHandler extends AbstractEtherRequestHandler {
    public SettingHandler(String url) {
        super(url);
    }

    @RequestMapping(method = {RequestMethod.POST})
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        Map<String, String> params = HttpRequestParser.parseParams(request);
        if (!params.containsKey("urlad")||!params.containsKey("urlad2")
                || !params.containsKey("resulturl") || !params.containsKey("startApp") || !params.containsKey("schooleNameLine1")
                || !params.containsKey("schooleNameLine2")) {
            response(response, "{\"success\":true, \"message\": \"参数不正确\"}");
            return;
        }
        String urlad = params.get("urlad");
        String urlad2 = params.get("urlad2");
        String resulturl = params.get("resulturl");
        String startApp = params.get("startApp");
        String schooleNameLine1 = params.get("schooleNameLine1");
        String schooleNameLine2 = params.get("schooleNameLine2");

        // 保存参数
        ShareUtils.put(EtherApp.context, "urlad", urlad);
        ShareUtils.put(EtherApp.context, "urlad2", urlad2);
        ShareUtils.put(EtherApp.context, "resulturl", resulturl);
        ShareUtils.put(EtherApp.context, "startApp", startApp);
        ShareUtils.put(EtherApp.context, "schooleNameLine1", schooleNameLine1);
        ShareUtils.put(EtherApp.context, "schooleNameLine2", schooleNameLine2);

        EventBus.getDefault().post(new SettingMessageEvent(urlad, urlad2, resulturl, schooleNameLine1, schooleNameLine2));

        response(response, "{\"success\":\"true\", \"message\": \"设置成功\"}");
    }

    private void response(HttpResponse response, String info) {
        StringEntity stringEntity = new StringEntity(info, "utf-8");
        response.setStatusCode(200);
        response.setEntity(stringEntity);
    }
}
