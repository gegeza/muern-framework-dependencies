package com.muern.framework.core.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gegeza
 * @date 2022/08/17
 */
public final class Maps {

    private Maps() {}

    public static <K, V> Map<K, V> emptyMap() {
        return new HashMap<>(2);
    }

    public static <K, V> Map<K, V> of(K k, V v) {
        Map<K, V> maps = new HashMap<>(4);
        maps.put(k, v);
        return maps;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
        Map<K, V> maps = new HashMap<>(4);
        maps.put(k1, v1);
        maps.put(k2, v2);
        return maps;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K, V> maps = new HashMap<>(4);
        maps.put(k1, v1);
        maps.put(k2, v2);
        maps.put(k3, v3);
        return maps;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Map<K, V> maps = new HashMap<>(4);
        maps.put(k1, v1);
        maps.put(k2, v2);
        maps.put(k3, v3);
        maps.put(k4, v4);
        return maps;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        Map<K, V> maps = new HashMap<>(4);
        maps.put(k1, v1);
        maps.put(k2, v2);
        maps.put(k3, v3);
        maps.put(k4, v4);
        maps.put(k5, v5);
        return maps;
    }
}
