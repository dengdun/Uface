package com.whzxw.uface.ether.http;

/**
 * 网络请求返回实体
 */
public class ResponseEntity<T> {
    private String success;
    private String message;
    private T result;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
