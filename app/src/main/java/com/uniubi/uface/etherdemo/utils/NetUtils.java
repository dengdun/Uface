package com.uniubi.uface.etherdemo.utils;

import android.util.Log;

import com.uniubi.uface.etherdemo.EtherApp;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 发送网络请求的
 */
public class NetUtils {

    public static void sendMessage(final String personId, final String faceId, final Float score, final String name, final String cardNo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("测试", (String)ShareUtils.get(EtherApp.context, "resulturl", ""));
                //接口地址
                String url_path = (String) ShareUtils.get(EtherApp.context, "resulturl", "");
                HttpURLConnection connection = null;
                try {
                    Log.i("测试", "1");
                    URL url = new URL(url_path);
                    connection = (HttpURLConnection) url.openConnection();
                    Log.i("测试", "2");
                    // 设置请求方式
                    connection.setRequestMethod("POST");
                    // 设置编码格式
                    connection.setRequestProperty("Charset", "UTF-8");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    Log.i("测试", "3");
//                    connection.addRequestProperty("Content-Type",
//                            "application/json;charset=utf-8");
                    // 传递自定义参数
                    StringBuilder stringBuilder = new StringBuilder()
                            .append("personId").append("=").append(personId)
                            .append("&")
                            .append("faceId").append("=").append(faceId)
                            .append("&")
                            .append("score").append("=").append(score)
                            .append("&")
                            .append("name").append("=").append(name)
                            .append("&")
                            .append("cardNo").append("=").append(cardNo);
                    Log.i("测试", "4");

//                    connection.setRequestProperty("personId", personId);
//                    connection.setRequestProperty("faceId", faceId);
//                    connection.setRequestProperty("score", score+"");
//                    connection.setRequestProperty("name", URLEncoder.encode( name, "utf-8"));
//                    connection.setRequestProperty("cardNo", cardNo);
                    // 设置容许输出
                    connection.setDoOutput(true);
                    Log.i("测试", "5");
                    DataOutput dataOutput = new DataOutputStream(connection.getOutputStream());
                    Log.i("测试", "6");
                    dataOutput.write(stringBuilder.toString().getBytes());
                    Log.i("测试", "7");
                    // 获取返回数据
                    if(connection.getResponseCode() == 200){
                        Log.i("测试", "8");
                        InputStream is = connection.getInputStream();
                        Log.i("测试", "9");
                    }
                    Log.i("测试", "10");
                } catch (MalformedURLException e) {
                    Log.i("测试", "报错1");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i("测试", "报错2");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    Log.i("测试", "关掉链接");
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
