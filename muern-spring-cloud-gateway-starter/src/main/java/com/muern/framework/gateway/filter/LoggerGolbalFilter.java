package com.muern.framework.gateway.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.muern.framework.core.common.Json;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记录请求及响应日志
 * 注意：Order必须小于-1，否则获取不到响应值
 * 参考：https://blog.csdn.net/m0_67400973/article/details/124348792
 * @author gegeza
 * @date 2022/08/26
 */
@Component
public class LoggerGolbalFilter implements GlobalFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerGolbalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取接口中body参数 这种方法获取requestBody必须在RouteLocator中配置readBody
        ServerHttpRequest request = exchange.getRequest();
        String params = Json.tostr(getParams(exchange));
        LOGGER.info(">>>> {} ==== {}", request.getPath(), params);
        //如果是json格式返回值 则打印接口响应值
        ServerHttpResponse originalResponse = exchange.getResponse();
        if (MediaType.APPLICATION_JSON.equals(originalResponse.getHeaders().getContentType())) {
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                @SuppressWarnings("unchecked")
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                        return super.writeWith(fluxBody.map(dataBuffer -> {
                            byte[] content = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(content);
                            DataBufferUtils.release(dataBuffer);
                            String R = new String(content, StandardCharsets.UTF_8);
                            LOGGER.info("<<<< {}", R);
                            byte[] uppedContent = new String(content, StandardCharsets.UTF_8).getBytes();
                            return bufferFactory.wrap(uppedContent);
                        }));
                    }
                    return super.writeWith(body);
                }
            };
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1;
    }

    public static Map<String, Object> getParams(ServerWebExchange exchange) {
        Map<String, Object> paramsMap = new HashMap<>(8);

        ServerHttpRequest request = exchange.getRequest();
        if (HttpMethod.GET.equals(request.getMethod())) {
            for (Map.Entry<String, List<String>> entry : request.getQueryParams().entrySet()) {
                paramsMap.put(entry.getKey(), entry.getValue().get(0));
            }
        } else {
            Object requestBody = exchange.getAttribute("cachedRequestBodyObject");
            paramsMap = Json.parse(Json.tostr(requestBody), new TypeReference<Map<String, Object>>() {});
        }

        return paramsMap;
    }
}
