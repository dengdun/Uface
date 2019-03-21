package com.uniubi.uface.etherdemo.utils;

import com.uniubi.uface.etherdemo.EtherApp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 发送网络请求的
 */
public class NetUtils {

    public static void sendMessage(final String personId, final String faceId, final Float score, final String name, final String cardNo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接口地址
                String url_path = (String) ShareUtils.get(EtherApp.context, "resulturl", "");
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(url_path);
                    connection = (HttpURLConnection) url.openConnection();

                    // 设置请求方式
                    connection.setRequestMethod("POST");
                    // 设置编码格式
                    connection.setRequestProperty("Charset", "UTF-8");
                    connection.addRequestProperty("Content-Type",
                            "application/json;charset=utf-8");
                    // 传递自定义参数
                    connection.setRequestProperty("personId", personId);
                    connection.setRequestProperty("faceId", faceId);
                    connection.setRequestProperty("score", score+"");
                    connection.setRequestProperty("name", URLEncoder.encode( name, "utf-8"));
                    connection.setRequestProperty("cardNo", cardNo);
                    // 设置容许输出
                    connection.setDoOutput(true);

                    PrintWriter out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(),"utf-8"));
                    out.flush();
                    out.close();

                    // 获取返回数据
                    if(connection.getResponseCode() == 200){
                        InputStream is = connection.getInputStream();

                    }
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static void startApp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接口地址
                String url_path = (String) ShareUtils.get(EtherApp.context, "startApp", "");
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(url_path);
                    connection = (HttpURLConnection) url.openConnection();
                    // 设置请求方式
                    connection.setRequestMethod("GET");
                    // 设置编码格式
                    connection.setRequestProperty("Charset", "UTF-8");
                    // 设置容许输出
                    connection.setDoOutput(true);

                    OutputStream os = connection.getOutputStream();

                    os.flush();
                    os.close();

                    // 获取返回数据
                    if(connection.getResponseCode() == 200){
                        InputStream is = connection.getInputStream();

                    }
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

}
