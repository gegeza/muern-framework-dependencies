package com.muern.framework.boot.filter;

import com.muern.framework.boot.utils.ResponseUtils;
import com.muern.framework.core.common.CodeImpl;
import com.muern.framework.core.common.Json;
import com.muern.framework.core.common.R;
import com.muern.framework.core.encrypt.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author gegeza
 * @date 2022/09/26
 */
@Component
@Order(Integer.MIN_VALUE + 200)
@ConditionalOnProperty(prefix = "muern.gateway.signature", name = "enable", havingValue = "true")
public class SignatureFilter extends OncePerRequestFilter {

    /** 请求失效时间15分钟 */
    private static final Long TIMEOUT = 15 * 60 * 1000L;

    @Value("${muern.gateway.signature.ignore-urls:null}")
    private List<String> ignoreUrls;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        for (String ignoreUrl : ignoreUrls) {
            if (request.getRequestURI().contains(ignoreUrl)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        //校验时间戳和签名值是否存在
        CachingContentRequestWrapper requestWrapper = (CachingContentRequestWrapper) request;
        String timestamp = requestWrapper.getParam("timestamp"), sign = requestWrapper.getParam("sign");
        if (!StringUtils.hasText(timestamp) || !StringUtils.hasText(sign)) {
            ResponseUtils.setResponse(response, CodeImpl.ERR_SIGN);
            return;
        }
        //校验timestamp是否超过十分钟
        if (System.currentTimeMillis() - Long.parseLong(timestamp) > TIMEOUT) {
            ResponseUtils.setResponse(response, R.ins(CodeImpl.ERR_TIME, System.currentTimeMillis()).toString());
            return;
        }
        //将所有参数放入TreeMap中去 将TreeMap中所有entry拼接成a=1&b=2&..&signKey=timestamp的格式
        Map<String, Object> params = new TreeMap<>(requestWrapper.getParams());
        String signStr = params.entrySet().stream()
                .filter(entry -> StringUtils.hasText(entry.getValue().toString()) && !"sign".equals(entry.getKey()))
                .map(entry -> entry.getKey().concat("=").concat(Json.tostr(entry.getValue())).concat("&"))
                .collect(Collectors.joining()).concat("signKey=").concat(timestamp);
        //如果签名不相等 返回错误信息
        if (!sign.equalsIgnoreCase(Hash.sha256(signStr))) {
            ResponseUtils.setResponse(response, R.ins(CodeImpl.ERR_SIGN, signStr.concat("-").concat(Hash.sha256(signStr))).toString());
            return;
        }
        //签名验证通过 继续接下来的请求
        filterChain.doFilter(request, response);
    }
}
