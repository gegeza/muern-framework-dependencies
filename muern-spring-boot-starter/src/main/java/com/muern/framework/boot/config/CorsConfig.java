package com.muern.framework.boot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * @author gegeza
 * @date 2022/08/16
 */
@ConditionalOnProperty(prefix = "muern.cors", name = "enable", havingValue = "true")
@Configuration
public class CorsConfig {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CorsConfig.class);

    @Value("${muern.cors.allowed-origins:*}") private List<String> allowedOrigins;
    @Value("${muern.cors.allowed-headers:*}") private List<String> allowedHeaders;
    @Value("${muern.cors.allowed-methods:*}") private List<String> allowedMethods;
    @Value("${muern.cors.exposed-headers:*}") private List<String> exposedHeaders;

    public CorsConfig() {
        LOGGER.info("CorsConfig init successful");
    }

    @Bean
    CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setMaxAge(24 * 60 * 60L);
        configuration.setAllowCredentials(Boolean.TRUE);
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setExposedHeaders(exposedHeaders);
        configuration.setAllowedMethods(allowedMethods);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }
}
