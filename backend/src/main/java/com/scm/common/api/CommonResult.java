package com.scm.common.api;

import com.scm.common.Result;

public class CommonResult<T> extends Result<T> {
    
    public CommonResult() {
        super(200, "success", null);
    }

    public CommonResult(int code, String message, T data) {
        super(code, message, data);
    }

    public static <T> CommonResult<T> success() {
        Result<T> result = Result.success();
        return convertFromResult(result);
    }

    public static <T> CommonResult<T> success(T data) {
        Result<T> result = Result.success(data);
        return convertFromResult(result);
    }

    public static <T> CommonResult<T> success(String message, T data) {
        Result<T> result = Result.success(data);
        result.setMessage(message);
        return convertFromResult(result);
    }

    public static <T> CommonResult<T> failed(String message) {
        Result<T> result = Result.error(1, message);
        return convertFromResult(result);
    }

    public static <T> CommonResult<T> failed(int code, String message) {
        Result<T> result = Result.error(code, message);
        return convertFromResult(result);
    }

    private static <T> CommonResult<T> convertFromResult(Result<T> result) {
        CommonResult<T> commonResult = new CommonResult<>(result.getCode(), result.getMessage(), result.getData());
        return commonResult;
    }
} 