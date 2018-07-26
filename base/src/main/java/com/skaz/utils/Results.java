package com.skaz.utils;

import com.skaz.bean.Result;

import java.util.Map;

public abstract class Results {

    public static <E> Result<E> newResult() {
        return new Result<E>();
    }

    public static <E> Result<E> newResult(Map<String, Object> properties) {
        Result<E> result = newResult();
        Objects.populate(result, properties);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <E> Result<E> newResult(boolean success, Integer code, E data, String msg) {
        if (null != data && data instanceof Result) {
            Result<E> result = newResult();
            Objects.copyNotNullProperty(result, (Result<E>) data);
            result.setSuccess(success);
            if (null != code) {
                result.code(code);
            }
            if (null != msg) {
                result.msg(msg);
            }
            return result;
        }
        return new Result<E>(success, code, data, msg);
    }

    //
    // 业务调用成功
    // --------------------------------------------------------------------------------------------------------------------------------
    public static <E> Result<E> success() {
        return success(null, null);
    }

    public static <E> Result<E> success(Integer code) {
        return success(code, null);
    }

    public static <E> Result<E> success(String msg) {
        return success(null, msg);
    }

    public static <E> Result<E> success(Integer code, String msg) {
        return successWithData(code, null, msg);
    }

    public static <E> Result<E> successWithData(E data) {
        return successWithData(null, data, null);
    }

    public static <E> Result<E> successWithData(E data, String msg) {
        return successWithData(null, data, msg);
    }

    public static <E> Result<E> successWithData(Integer code, E data) {
        return successWithData(code, data, null);
    }

    public static <E> Result<E> successWithData(Integer code, E data, String msg) {
        return newResult(true, code, data, msg);
    }

    //
    // 业务调用失败
    // --------------------------------------------------------------------------------------------------------------------------------
    public static <E> Result<E> failure() {
        return failure(null, null);
    }

    public static <E> Result<E> failure(Integer code) {
        return failure(code, null);
    }

    public static <E> Result<E> failure(String msg) {
        return failure(null, msg);
    }

    public static <E> Result<E> failure(Integer code, String msg) {
        return failureWithData(code, null, msg);
    }

    public static <E> Result<E> failureWithData(E data) {
        return failureWithData(null, data, null);
    }

    public static <E> Result<E> failureWithData(E data, String msg) {
        return failureWithData(null, data, msg);
    }

    public static <E> Result<E> failureWithData(Integer code, E data) {
        return failureWithData(code, data, null);
    }

    public static <E> Result<E> failureWithData(Integer code, E data, String msg) {
        return newResult(false, code, data, msg);
    }

    //
    // 代码执行失败:返回包含错误提示
    // --------------------------------------------------------------------------------------------------------------------------------
    public static <E> Result<E> failureWithErrors(Throwable ex) {
        return failureWithErrors(null, null, ex.getMessage());
    }

    public static <E> Result<E> failureWithErrors(String field, String message) {
        Map<String, Object> errors = Maps.newHashMap();
        errors.put(field, message);
        return failureWithErrors(null, errors, message);
    }

    public static <E> Result<E> failureWithErrors(Map<String, Object> errors, String message) {
        return failureWithErrors(null, errors, message);
    }

    public static <E> Result<E> failureWithErrors(Integer code, Map<String, Object> errors, String message) {
        return new Result(code, errors, message);
    }

}
