package com.muern.framework.boot.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author gegeza
 * @date 2020-08-19
 */
@Component
public class RedisHelper {
    /**
     * 释放锁脚本，原子操作
     */
    public static final String UNLOCK_LUA = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";


    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /** 获取缓存字符串 */
    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /** 设置缓存字符串并设置过期时间(秒) */
    public void setString(String key, String value, long seconds) {
        stringRedisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    /** 获取对象 */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /** 设置缓存对象 */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /** 设置缓存对象并设置过期时间(秒) */
    public void set(String key, Object value, long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    /** value++ */
    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /** value-- */
    public Long decr(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    /** value增加{delta}  */
    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /** value减少{delta} */
    public Long decr(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /** 更新key剩余时间，单位：秒 */
    public Boolean expire(String key, long seconds) {
        return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    /** 查询key剩余时间，单位：秒 */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /** 删除对应的key */
    public void del(String key) {
        redisTemplate.delete(key);
    }

    /** 批量删除对应的value */
    public void del(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /** 根据正则批量删除key */
    public void delPattern(final String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /** 判断缓存中是否有对应的value */
    public boolean hasKey(final String key) {
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }

    /**
     * Key不存在则设置并返回true，否则不设置并返回false
     */
    public boolean setNX(String key, Object value) {
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(key, value);
        return setIfAbsent != null && setIfAbsent;
    }

    /**
     * Key不存在则设置并返回true，否则不设置并返回false 单位：秒
     */
    public boolean setNX(String key, Object value, long seconds) {
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(key, value, seconds, TimeUnit.SECONDS);
        return setIfAbsent != null && setIfAbsent;
    }



    /**
     * 加分布式锁
     *
     * @param key       锁名称
     * @param requestId 锁钥匙
     * @param timeout   单位：秒
     * @return boolean 是否加锁成功
     */
    public Boolean tryLock(String key, String requestId, long timeout) {
        return redisTemplate.opsForValue().setIfAbsent(key, requestId, timeout, TimeUnit.SECONDS);
    }

    /**
     * 分布式解锁
     *
     * @param key       锁名称
     * @param requestId 锁钥匙
     * @return boolean 是否解锁成功
     */
    public Boolean releaseLock(String key, String requestId) {
        Long l = redisTemplate.execute(new DefaultRedisScript<>(UNLOCK_LUA, Long.class), Collections.singletonList(key), requestId);
        return l != null && l.equals(1L);
    }
}
