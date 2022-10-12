package com.muern.framework.boot.redis;

/**
 * @author gegeza
 * @date 2022/08/29
 */
public final class RedisKey {
    public static final String VERIFY_CODE_KEY = "MUERN:VERIFY:";
    public static final String IDEMPOTENT_KEY = "MUERN:SIGN:";
    public static final String TOEKN_USER_KEY = "TOKEN:USER:";
}
