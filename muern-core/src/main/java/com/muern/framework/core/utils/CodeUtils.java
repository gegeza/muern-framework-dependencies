package com.muern.framework.core.utils;

import com.github.f4b6a3.ulid.UlidCreator;

/**
 * @author gegeza
 * @date 2022/11/02
 */
public final class CodeUtils {

    public static String getCode(int length) {
        return String.format("%06d", Double.valueOf(Math.random() * Math.pow(10, length)).intValue());
    }

    public static String getUlid() {
        return UlidCreator.getUlid().toLowerCase();
    }
}
