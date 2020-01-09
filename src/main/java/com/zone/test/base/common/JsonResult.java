package com.zone.test.base.common;

import java.io.Serializable;

/**
 * 2019/7/13 14:48
 *
 * @author owen pan
 */
public class JsonResult<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;

    JsonResult(Builder<T> builder) {
        this.code = builder.code;
        this.msg = builder.msg;
        this.data = builder.data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static class Builder<T> {
        private Integer code=200;
        private String msg="";
        private T data;

        public Builder<T> code(Integer code) {
            this.code = code;
            return this;
        }

        public Builder<T> msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public JsonResult<T> build() {
            return new JsonResult<T>(this);
        }
    }
}
