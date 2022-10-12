package com.muern.framework.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 通过校验sign值在redis中是否存在实现接口幂等性
 * @author gegeza
 * @date 2022/08/28
 */
@Component
public class IdempotentGlobalFilter implements GlobalFilter, Ordered {

    @Resource private StringRedisTemplate stringRedisTemplate;

    public static final String REPEAT_KEY = "MUERN:SIGN:";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        boolean ignoreSignatureFlag = exchange.getAttribute(SignatureGlobalFilter.IGNORE_SIGNATURE_FLAG);
        if (ignoreSignatureFlag) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        //如果是post 则放入签名
        if (HttpMethod.POST.equals(request.getMethod())) {
            //获取请求签名
            Map<String, Object> paramsMap = LoggerGolbalFilter.getParams(exchange);
            if (!paramsMap.containsKey("sign")) {
                return OptionGlobalFilter.fastFinish(exchange, "timestamp or sign is empty", null);
            }
            String sign = String.valueOf(paramsMap.get("sign")), signKey = REPEAT_KEY.concat(sign);
            Boolean hasKey = stringRedisTemplate.hasKey(signKey);
            if (hasKey != null && hasKey) {
                // 快速返回,不调用业务系统
                return OptionGlobalFilter.fastFinish(exchange, "repeat request", null);
            }
            stringRedisTemplate.opsForValue().set(signKey, sign, 10, TimeUnit.MINUTES);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 3;
    }
}
