package com.whzxw.uface.ether.utils;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.whzxw.uface.ether.EtherApp;
import com.whzxw.uface.ether.http.ResponseEntity;
import com.whzxw.uface.ether.http.RetrofitManager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 发送网络请求的
 */
public class NetHttpUtil {
    private static final String PREFIX = "--";                            //前缀
    private static final String BOUNDARY = UUID.randomUUID().toString();  //边界标识 随机生成
    private static final String LINE_END = "\r\n";

    public static void sendMessage(final String personId, final String faceId, final Float score, final String name, final String cardNo, final Bitmap face) {
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
                    if (face != null) {
                        String facebase64 = bitmapToBase64(face);
                        stringBuilder.append("&").append("face").append("=").append(facebase64);
                    }
                    // 设置容许输出
                    connection.setDoOutput(true);
                    DataOutputStream dataOutput = new DataOutputStream(connection.getOutputStream());
                    dataOutput.write(stringBuilder.toString().getBytes());
//                    if (face != null) {
//                        StringBuilder fileSb = new StringBuilder();
//                        fileSb.append(PREFIX)
//                                .append(BOUNDARY)
//                                .append(LINE_END)
//                                .append("Content-Disposition: form-data; name=\"file\"; filename=\"pic.jpg\"" + LINE_END)
//                                .append("Content-Type: image/jpg" + LINE_END) //此处的ContentType不同于 请求头 中Content-Type
//                                .append("Content-Transfer-Encoding: 8bit" + LINE_END)
//                                .append(LINE_END);
//                        dataOutput.writeBytes(fileSb.toString());
//                        dataOutput.write(face);
//                        dataOutput.writeBytes(LINE_END);
//                    }
//                    dataOutput.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_END);
                    dataOutput.flush();
                    dataOutput.close();
//                    if (face != null) face.recycle();
                    // 获取返回数据
                    if(connection.getResponseCode() == 200){
                        InputStream is = connection.getInputStream();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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

    /**
     * startApp这没有链接上的话，就要一直尝试链接树莓派,发送已经开机上线的通知
     */
    public static void startRepeatApp() {
        String startAppUrl = ShareferenceManager.getStartAppUrl();
        // 把整个地址中的host替换成空字符串替换过来

        RetrofitManager.getInstance()
                .apiService
                .startApp(startAppUrl)
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final Observable<Throwable> throwableObservable) throws Exception {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                if (throwable instanceof IOException) {
                                    return Observable.just(1).delay(2, TimeUnit.SECONDS);
                                } else {
                                    return Observable.error(new Throwable(""));
                                }
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ResponseEntity>() {
                    @Override
                    public void accept(ResponseEntity requestEntity) throws Exception {
                        Log.i("jin", "get result");
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("jin", "throw erro");
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i("jin", "run throw erro");
                    }
                });


    }


    /**
     * 把图片转base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.URL_SAFE | Base64.NO_WRAP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
