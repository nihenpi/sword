package com.skaz.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.Map;

@Data
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Result<E> extends BaseBean {

    private static final long serialVersionUID = -333987130291467588L;
    public boolean             success;          // 成功标志
    public String              msg;              // 相关消息
    public Integer             code;             // 返回标示
    public E                   data;             // 相关数据
    public Map<String, Object> errors;           // 错误详细

    public Result() {
        super();
    }

    public Result(boolean success) {
        this.success = success;
    }

    public Result(boolean success, Integer code, E data, String msg) {
        this(success);
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Result(Integer code, Map<String, Object> errors, String msg) {
        this.success = false;
        this.msg = msg;
        this.errors = errors;
        this.code = code;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public boolean hasErrors() {
        if (this.errors != null && this.errors.size() > 0) {
            return true;
        }
        return false;
    }

    public Result<E> success(boolean success) {
        this.success = success;
        return this;
    }

    public Result<E> code(Integer code) {
        this.code = code;
        return this;
    }

    public Result<E> data(E data) {
        this.data = data;
        return this;
    }

    public Result<E> msg(String msg) {
        this.msg = msg;
        return this;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(E data) {
        this.data = data;
    }

    public Result<E> errors(Map<String, Object> errors) {
        this.errors = errors;
        return this;
    }

}
