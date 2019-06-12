package com.uniubi.uface.ether.http;

/**
 * 网络请求返回实体
 */
public class ResponseEntity {
    boolean success;
    String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
