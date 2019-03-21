package com.uniubi.uface.etherdemo.serverhandle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import com.uniubi.faceapi.CvFace;
import com.uniubi.uface.ether.andserver.handler.AbstractEtherRequestHandler;
import com.uniubi.uface.ether.config.configenum.algorithm.FaceOrientation;
import com.uniubi.uface.ether.core.EtherFaceManager;
import com.uniubi.uface.ether.core.cvhandle.FaceHandler;
import com.uniubi.uface.ether.core.exception.CvFaceException;
import com.uniubi.uface.ether.db.OfflineFaceInfo;
import com.uniubi.uface.ether.db.impl.OfflineFaceInfoImpl;
import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;

import java.io.IOException;
import java.util.Map;

/**
 * description:人脸添加接口
 * version:
 * email: cfj950221@163.com
 *
 * @author caojun
 * @date 2018/8/15
 */

public class FaceCreateHandler extends AbstractEtherRequestHandler {

    private FaceHandler handler = null;

    public FaceCreateHandler(String url) {
        super(url);

    }

    @RequestMapping(method = {RequestMethod.POST})
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

        Map<String, String> params = HttpRequestParser.parseParams(request);
        if (!params.containsKey("faceId") || !params.containsKey("personId") || !params.containsKey("imgBase64")
                || !params.containsKey("imagePath") || !params.containsKey("name")|| !params.containsKey("cardNo")) {
            response(response, "Please fill in whole params");
            return;
        }
        String faceId = params.get("faceId");
        String personId = params.get("personId");
        String imgBase64 = params.get("imgBase64");
        String imagePath = params.get("imagePath");
        String name = params.get("name");
        String cardNo = params.get("cardNo");
        // 把personID 姓名/ faceId存在一个里面  三个字段都拼接到一个字段里面
        personId += personId + "/" + name + "/" + faceId;
        faceId = cardNo;
        if (!TextUtils.isEmpty(faceId) && !TextUtils.isEmpty(personId)) {
            if (TextUtils.isEmpty(imgBase64)) {
                return;
            }
            byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if (bitmap != null) {
                if (handler == null) {
                    handler = new FaceHandler();
                    handler.init();
                }
                try {
                    CvFace[] cvFaces = handler.detectBGR(bitmap, FaceOrientation.CV_FACE_UP);
                    byte[] feature = handler.getFeatureBGR(bitmap, cvFaces[0]);
                    OfflineFaceInfo info = new OfflineFaceInfo();
                    info.setFeature(feature);
                    info.setFaceId(faceId);
                    info.setPersonId(personId);
                    info.setImgPath(imagePath);
                    OfflineFaceInfoImpl.getFaceInfoImpl().saveFaceInfo(info);

                    EtherFaceManager.getInstance().addFeatureToLib(info.getFeature(), info.getPersonId(), info.getFaceId());
                    response(response, "{\"success\":true, \"message\": \"添加人脸成功\"}");
                } catch (CvFaceException e) {
                    e.printStackTrace();
                    response(response, "{\"success\":false, \"message\": \"图片中未检测到人脸\"}");
                }

            } else {
                response(response, "{\"success\":false, \"message\": \"图片解析异常\"}");
            }
        }

    }

    private void response(HttpResponse response, String info) {
        StringEntity stringEntity = new StringEntity(info, "utf-8");
        response.setStatusCode(200);
        response.setEntity(stringEntity);
    }
}
