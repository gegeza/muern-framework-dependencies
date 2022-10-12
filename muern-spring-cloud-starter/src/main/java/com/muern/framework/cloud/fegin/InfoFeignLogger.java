package com.muern.framework.cloud.fegin;

import feign.Request;
import feign.Response;
import feign.Util;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author gegeza
 * @date 2022/08/29
 */
public class InfoFeignLogger extends feign.Logger {

    private final Logger LOGGER;

    public InfoFeignLogger(Logger logger) {
        this.LOGGER = logger;
    }

    @Override
    protected void log(String s, String s1, Object... objects) {}

    @Override
    protected void logRequest(String configKey, feign.Logger.Level logLevel, Request request) {
        LOGGER.info("Fegin ----> {} {}", request.httpMethod().name(), request.url());
        byte[] bodyData = request.body();
        if (bodyData != null) {
            LOGGER.info("BODY {}", new String(bodyData, StandardCharsets.UTF_8));
        }
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        byte[] bodyData = Util.toByteArray(response.body().asInputStream());
        if (bodyData.length > 0) {
            LOGGER.info("Fegin <---- {}ms {}", elapsedTime, new String(bodyData, StandardCharsets.UTF_8));
        }
        return response.toBuilder().body(bodyData).build();
    }
}
