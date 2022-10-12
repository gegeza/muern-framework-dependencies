package com.muern.framework.boot.filter;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @author gegeza
 * @date 2022/08/28
 */
@Component
@Order(Integer.MIN_VALUE)
public class TraceidFilter extends OncePerRequestFilter{

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取TraceId变量并放入日志MDC中
        String traceId = request.getHeader("x-trace-id");
        traceId = StringUtils.hasText(traceId) ? traceId : UUID.randomUUID().toString();
        MDC.put("traceid", traceId);
        filterChain.doFilter(request, response);
    }
}
