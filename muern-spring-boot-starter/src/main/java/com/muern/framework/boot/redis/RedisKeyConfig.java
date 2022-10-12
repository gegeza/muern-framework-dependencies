package com.muern.framework.boot.redis;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.Ordered;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 提供默认key配置项
 * @author gegeza
 * @date 2022/09/02
 */
public interface RedisKeyConfig {
    Map<String, RedisCacheConfiguration> keyConfig();
}

@Component
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnMissingBean(RedisKeyConfig.class)
class DefaultRedisKeyConfig implements RedisKeyConfig {
    @Override
    public Map<String, RedisCacheConfiguration> keyConfig() {
        return new HashMap<>();
    }
}