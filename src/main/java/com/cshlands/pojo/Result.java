package com.cshlands.pojo;


//统一响应结果
public class Result<T> {
    private int status;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setStatus(200);
        r.setMsg("success");
        r.setData(data);
        return r;
    }

    public static Result<Void> success() {
        return success(null);
    }

    public static <T> Result<T> error(int code, String msg) {
        Result<T> r = new Result<>();
        r.setStatus(code);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
