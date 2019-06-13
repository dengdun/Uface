package com.whzxw.uface.ether.serverhandle;

import com.whzxw.uface.ether.EtherApp;
import com.uniubi.uface.ether.andserver.handler.AbstractEtherRequestHandler;
import com.uniubi.uface.ether.core.EtherFaceManager;
import com.whzxw.uface.ether.database.PersonTable;
import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.annotation.RequestMapping;

import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;

import java.io.IOException;

/**
 * description:
 * version:
 * email: cfj950221@163.com
 * 删除全部的信息
 * @author caojun
 * @date 2018/8/15
 */

public class FaceAllDeleteHandler extends AbstractEtherRequestHandler {

    public FaceAllDeleteHandler(String url) {
        super(url);
    }

    @RequestMapping(method = {RequestMethod.GET})
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

        EtherFaceManager.getInstance().removeAll();
        EtherFaceManager.getInstance().removeVerifyData();
        EtherApp.daoSession.deleteAll(PersonTable.class);
        response(response,"{\"success\":true, \"message\": \"格式化设备成功\"}");
    }

    private void response(HttpResponse response, String info) {
        StringEntity stringEntity = new StringEntity(info, "utf-8");
        response.setStatusCode(200);
        response.setEntity(stringEntity);
    }
}
