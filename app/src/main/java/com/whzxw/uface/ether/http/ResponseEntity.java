package com.whzxw.uface.ether.http;

/**
 * 网络请求返回实体
 */
public class ResponseEntity {
    private String success;
    private String message;
    private String result;

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


}
