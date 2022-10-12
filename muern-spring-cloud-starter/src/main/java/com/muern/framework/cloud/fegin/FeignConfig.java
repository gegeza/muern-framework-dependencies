package com.muern.framework.cloud.fegin;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/**
 * @author gegeza
 * @date 2022/08/29
 */
@Configuration
public class FeignConfig implements RequestInterceptor {

    @Bean
    Logger.Level feignLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    Logger infoFeignLoggerFactory() {
        return new InfoFeignLogger(LoggerFactory.getLogger(FeignConfig.class));
    }

    /** 超时时间配置 */
    @Bean
    public Request.Options options() {
        return new Request.Options(10, TimeUnit.SECONDS, 10, TimeUnit.SECONDS, true);
    }

    /** Fegin调用时传递请求头 */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headName = headerNames.nextElement();
                if ("content-length".equals(headName)){
                    continue;
                }
                requestTemplate.header(headName, request.getHeader(headName));
            }
        }

    }
}
