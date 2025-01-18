package com.scm.utils;

/**
 * 统一返回结果类
 */
public class Result<T> {
    private int code;  // 状态码：0-成功，非0-失败
    private String message;  // 提示信息
    private T data;  // 数据

    public Result() {
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result<>(0, "success");
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(-1, message);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
} 