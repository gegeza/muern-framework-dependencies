package com.muern.framework.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 请求头和响应头中添加traceid
 * @author gegeza
 * @date 2022/08/28
 */
@Component
public class TraceidGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //生成traceid
        String traceId = UUID.randomUUID().toString();
        //请求头重添加traceid
        ServerHttpRequest request = exchange.getRequest().mutate().header("x-trace-id", new String[]{traceId}).build();
        //响应头中添加traceid
        exchange.getResponse().getHeaders().add("x-trace-id", traceId);
        //通过请求
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 4;
    }
}
