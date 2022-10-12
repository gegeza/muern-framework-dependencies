package com.muern.framework.boot.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.muern.framework.core.common.Code;
import com.muern.framework.core.common.Json;
import com.muern.framework.core.common.R;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author gegeza
 * @date 2020-04-20
 */
public class ResponseUtils {

    public static final Logger log = LoggerFactory.getLogger(ResponseUtils.class);

    public static String getResponse(HttpServletRequest request) {
        try (
            InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(inputStreamReader)
        ) {
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static Map<String, Object> getRequestMap(HttpServletRequest request) {
        String data = getResponse(request);
        return StringUtils.hasText(data) ? Json.parse(data, new TypeReference<Map<String, Object>>() {}) : null;
    }

    public static void setResponse(HttpServletResponse response, String message) {
        try {
            response.setStatus(200);
            response.setContentType(ContentType.APPLICATION_JSON.toString());
            PrintWriter out = response.getWriter();
            out.println(message);
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }

    public static void setResponse(HttpServletResponse response, Code code) {
        setResponse(response, R.ins(code).toString());
    }
}
