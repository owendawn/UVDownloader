package com.zone.uvdownloader.base.common;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;

/**
 * 2019/7/13 16:30
 *
 * @author owen pan
 */
public class JsonPageResult<T>  implements Serializable {
    private Integer code;
    private String msg;
    private Page page;
    private T data;

    JsonPageResult(Builder<T> builder) {
        this.page = builder.page;
        this.data = builder.data;
        this.code=builder.code;
        this.msg=builder.msg;
    }

    public Page getPage() {
        return page;
    }

    public T getData() {
        return data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static class Builder<T> implements Serializable{
        private Integer code=200;
        private String msg="";
        private Page page;
        private T data;

        public Builder<T> page(IPage page) {
            this.page = new Page()
                    .pageSize(Long.valueOf(page.getSize()).intValue())
                    .pageNum(Long.valueOf(page.getCurrent()).intValue())
                    .total(page.getTotal());
            return this;
        }
        public Builder<T> page(Page page) {
            this.page = page;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> code(Integer code) {
            this.code = code;
            return this;
        }

        public Builder<T> msg(String msg) {
            this.msg = msg;
            return this;
        }

        public JsonPageResult<T> build() {
            return new JsonPageResult<T>(this);
        }
    }

    public static class Page {
        private Integer pageNum;
        private Integer pageSize;
        private Long total;

        public Page pageNum(Integer pageNum) {
            this.pageNum = pageNum;
            return this;
        }

        public Page pageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Page total(Long total) {
            this.total = total;
            return this;
        }

        public Integer getPageNum() {
            return pageNum;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public Long getTotal() {
            return total;
        }
    }
}
