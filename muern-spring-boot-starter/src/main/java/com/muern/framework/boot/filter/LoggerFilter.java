package com.muern.framework.boot.filter;

import com.muern.framework.core.common.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author gegeza
 * @date 2022/08/28
 */
@Component
@Order(Integer.MIN_VALUE + 1)
public class LoggerFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //设置UTF-8编码
        request.setCharacterEncoding("UTF-8");
        //解决上传无法获取到文件问题
        if (request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) {
            request.getParts();
        }
        //包装request以及response
        CachingContentRequestWrapper requestWrapper = new CachingContentRequestWrapper(request);
        CachingContentResponseWrapper responseWrapper = new CachingContentResponseWrapper(response);
        //获取请求日志并打印 截取前500位长度的参数
        LOGGER.info("====> {} ====> {}", requestWrapper.getRequestURI(), Json.tostr(requestWrapper.getParams()));
        //发起请求
        filterChain.doFilter(requestWrapper, responseWrapper);
        LOGGER.info("<==== {}", isEnoughSubstr(responseWrapper.getResponseContent()));
    }

    private String isEnoughSubstr(String params) {
        return params.length() <= 500 ? params : params.substring(0, 500);
    }
}
