package com.muern.framework.boot.filter;

import com.muern.framework.boot.redis.RedisKey;
import com.muern.framework.boot.utils.ResponseUtils;
import com.muern.framework.core.common.CodeImpl;
import com.muern.framework.core.common.R;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author gegeza
 * @date 2022/09/26
 */
@Component
@Order(Integer.MIN_VALUE + 300)
@ConditionalOnProperty(prefix = "muern.gateway.idempoten", name = "enable", havingValue = "true")
public class IdempotentFilter extends OncePerRequestFilter {

    /** 请求失效时间15分钟 */
    private static final Long TIMEOUT = 15 * 60 * 1000L;

    @Resource
    private RedisTemplate<String, Long> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //校验时间戳和签名值是否存在
        CachingContentRequestWrapper requestWrapper = (CachingContentRequestWrapper) request;
        //通过redis实现POST接口的幂等性
        if (HttpMethod.POST.matches(request.getMethod()) && StringUtils.hasText(requestWrapper.getParam("sign"))) {
            //如果签名相等则判断sign是否存在于redis中
            String sign = requestWrapper.getParam("sign");
            Boolean hasKey = redisTemplate.hasKey(RedisKey.IDEMPOTENT_KEY.concat(sign));
            if (hasKey != null && hasKey) {
                ResponseUtils.setResponse(response, R.ins(CodeImpl.ERR_REPEATED).toString());
                return;
            }
            //不存在则存入redis中
            redisTemplate.opsForValue().set(RedisKey.IDEMPOTENT_KEY.concat(sign), System.currentTimeMillis(), TIMEOUT, TimeUnit.MILLISECONDS);
        }
        //签名验证通过 继续接下来的请求
        filterChain.doFilter(request, response);
    }
}
