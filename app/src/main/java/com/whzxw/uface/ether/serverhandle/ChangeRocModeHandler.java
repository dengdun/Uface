package com.whzxw.uface.ether.serverhandle;

import com.uniubi.uface.ether.andserver.handler.AbstractEtherRequestHandler;
import com.uniubi.uface.ether.base.UfaceEtherImpl;
import com.uniubi.uface.ether.config.ServiceOptions;
import com.uniubi.uface.ether.config.configenum.service.RecoPattern;
import com.uniubi.uface.ether.core.EtherFaceManager;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;

import java.io.IOException;
import java.util.Map;

/**
 * @author qiaopeng
 * @date 2018/8/31
 */
public class ChangeRocModeHandler extends AbstractEtherRequestHandler {

    ServiceOptions serviceOptions;

    public ChangeRocModeHandler(String url) {
        super(url);
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        Map<String, String> params = HttpRequestParser.parseParams(request);
        if (!params.containsKey("mode")) {
            response(response, "Please fill in whole params");
            return;
        }
       serviceOptions = UfaceEtherImpl.getServiceOptions();
        String mode = params.get("mode");
        if (mode.equals("1")){
            serviceOptions.setRecoPattern(RecoPattern.VERIFY);
        }else{
            serviceOptions.setRecoPattern(RecoPattern.IDENTIFY);
            EtherFaceManager.getInstance().reLoadFeatureFromDb();
        }
        response(response, "切换成功");
    }

    private void response(HttpResponse response, String info) {
        StringEntity stringEntity = new StringEntity(info, "utf-8");
        response.setStatusCode(200);
        response.setEntity(stringEntity);
    }
}
