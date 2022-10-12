package com.muern.framework.gateway.filter;

import com.muern.framework.core.common.Json;
import com.muern.framework.core.encrypt.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 验证时间戳和签名
 * @author gegeza
 * @date 2022/08/26
 */
@Component
public class SignatureGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignatureGlobalFilter.class);

    /** 请求时间误差：10分钟 */
    private static final Long TIMEOUT = 10 * 60 * 1000L;
    public static final String IGNORE_SIGNATURE_FLAG = "IGNORE_SIGNATURE_FLAG";

    @Value("${muern.gateway.signature.ignore-urls}")
    private List<String> ignoreUrls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        boolean ignoreSignatureFlag = hasIgnore(exchange.getRequest().getURI().toString());
        exchange.getAttributes().put(IGNORE_SIGNATURE_FLAG, ignoreSignatureFlag);
        if (ignoreSignatureFlag) {
            return chain.filter(exchange);
        }
        //获取请求参数
        Map<String, Object> paramsMap = LoggerGolbalFilter.getParams(exchange);
        if (!paramsMap.containsKey("timestamp") || !paramsMap.containsKey("sign")) {
            return OptionGlobalFilter.fastFinish(exchange, "timestamp or sign is empty", null);
        }
        //校验时间戳和签名值是否存在
        String timestamp = paramsMap.get("timestamp").toString(), sign = paramsMap.get("sign").toString();
        if (!StringUtils.hasLength(timestamp) || !StringUtils.hasLength(sign)) {
            return OptionGlobalFilter.fastFinish(exchange, "timestamp or sign is empty", null);
        }
        //校验timestamp是否超过十分钟
        if (System.currentTimeMillis() - Long.parseLong(timestamp) > TIMEOUT) {
            return OptionGlobalFilter.fastFinish(exchange, "timestamp error", String.valueOf(System.currentTimeMillis()));
        }
        //将所有参数放入TreeMap中去 将TreeMap中所有entry拼接成a=1&b=2&..&signKey=timestamp的格式
        Map<String, Object> params = new TreeMap<>(paramsMap);
        String signStr = params.entrySet().stream()
                .filter(entry -> StringUtils.hasLength(entry.getValue().toString()) && !"sign".equals(entry.getKey()))
                .map(entry -> entry.getKey().concat("=").concat(Json.tostr(entry.getValue())).concat("&"))
                .collect(Collectors.joining()).concat("signKey=").concat(timestamp);
        //如果签名不相等 返回错误信息
        if (!sign.equalsIgnoreCase(Hash.sha256(signStr))) {
            LOGGER.error("sign error: {}", signStr);
            return OptionGlobalFilter.fastFinish(exchange, "sign error", signStr.concat("-").concat(Hash.sha256(signStr)));
        }
        return chain.filter(exchange);
    }

    private boolean hasIgnore(String uri) {
        for (String ignoreUrl : ignoreUrls) {
            if (uri.contains(ignoreUrl)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 2;
    }
}
