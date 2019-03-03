package com.uniubi.uface.etherdemo.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.aliyun.alink.linksdk.tools.ThreadTools.runOnUiThread;

public class NetUtils {

    public static void sendMessage(String personId, String faceId, Integer score) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接口地址
                String url_path = "http://223.111.182.5:8080/logistics/goods/myOffer";
                try{
                    //使用该地址创建一个 URL 对象
                    URL url = new URL(url_path);
                    //使用创建的URL对象的openConnection()方法创建一个HttpURLConnection对象
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    /**
                     * 设置HttpURLConnection对象的参数
                     */
                    // 设置请求方法为 GET 请求
                    httpURLConnection.setRequestMethod("GET");
                    //使用输入流
                    httpURLConnection.setDoInput(true);
                    //GET 方式，不需要使用输出流
                    httpURLConnection.setDoOutput(false);
                    //设置超时
                    httpURLConnection.setConnectTimeout(10000);
                    httpURLConnection.setReadTimeout(1000);
                    //连接
                    httpURLConnection.connect();
                    //还有很多参数设置 请自行查阅
                    //连接后，创建一个输入流来读取response
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"utf-8"));
                    String line = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    String response = "";
                    //每次读取一行，若非空则添加至 stringBuilder
                    while((line = bufferedReader.readLine()) != null){
                        stringBuilder.append(line);
                    }
                    //读取所有的数据后，赋值给 response
                    response = stringBuilder.toString().trim();

                    final String finalResponse = response;
                    //切换到ui线程更新ui
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                    bufferedReader.close();
                    httpURLConnection.disconnect();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
