package com.muern.framework.core.common;

/**
 * @author gegeza
 * @date 2022/08/17
 */
public enum CodeImpl implements Code {
    /** 操作成功 */
    OK("000000", "操作成功"),
    /** 操作失败 */
    FAIL("000001", "操作失败"),
    /** 令牌错误 */
    ERR_TOKEN("000002", "令牌错误"),
    /** 数据库异常 */
    ERR_DB("000003", "数据库异常"),
    /** 签名错误 */
    ERR_SIGN("000004", "签名错误"),
    /** 时间戳错误 */
    ERR_TIME("000005", "时间错误"),
    /** 重复调用 */
    ERR_REPEATED("000006", "请勿重复调用接口"),
    /** 重复调用 */
    ERR_PERMISSION("000007", "权限校验失败"),
    ;

    private final String code;
    private final String desc;

    CodeImpl(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}