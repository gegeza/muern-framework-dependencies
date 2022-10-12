package com.muern.framework.core.common;

import com.muern.framework.core.utils.StringUtils;

import java.io.Serializable;

/**
 * @author gegeza
 * @date 2022/10/11
 */
public class R<T> implements Serializable {

    private static final long serialVersionUID = 100000000000000000L;

    private String code;
    private String desc;
    private T data;

    public R() {}

    public R(String code, String desc, T data) {
        this.code = code;
        this.desc = desc;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return Json.tostr(this);
    }

    public static <T> R<Void> ok() {
        return ins(CodeImpl.OK);
    }

    public static <T> R<T> ok(T t) {
        return ins(CodeImpl.OK, t);
    }

    public static <T> R<T> fail() {
        return ins(CodeImpl.FAIL);
    }

    public static <T> R<T> fail(String desc) {
        return ins(CodeImpl.FAIL.getCode(), desc);
    }

    public static <T> R<T> ins(Code code) {
        return ins(code, null);
    }

    public static <T> R<T> ins(Code code, T t) {
        return ins(code.getCode(), code.getDesc(), t);
    }

    public static <T> R<T> ins(String code, String desc) {
        return ins(code, desc, null);
    }

    public static <T> R<T> ins(String code, String desc, T t) {
        return new R<>(code, desc, t);
    }

    /** 用于判断当前Result 是否是成功的 */
    public boolean succ() {
        return CodeImpl.OK.getCode().equals(this.code);
    }

    public <T> T data(Class<T> clazz) {
        return succ() && data != null && StringUtils.hasText(data.toString()) ? Json.parse(Json.tostr(this.data), clazz) : null;
    }
}
