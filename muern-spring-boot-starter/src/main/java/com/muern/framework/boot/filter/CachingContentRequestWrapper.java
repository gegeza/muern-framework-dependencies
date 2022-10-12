package com.muern.framework.boot.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.muern.framework.core.common.Json;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gegeza
 * @date 2019-11-06 4:56 PM
 */
public class CachingContentRequestWrapper extends HttpServletRequestWrapper {

    private byte[] body;

    public CachingContentRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = StreamUtils.copyToByteArray(request.getInputStream());
    }

    private boolean isJsonPost() {
        return StringUtils.hasText(this.getMethod()) && "POST".equals(this.getMethod())
                && StringUtils.hasText(this.getContentType()) && this.getContentType().startsWith("application/json");
    }

    public Map<String, Object> getParams() {
        if (isJsonPost()) {
            String bodyStr = new String(body, StandardCharsets.UTF_8);
            return Json.parse(bodyStr, new TypeReference<Map<String, Object>>() {});
        } else {
            Map<String, Object> map = new HashMap<>(8);
            for (Map.Entry<String, String[]> entry : getParameterMap().entrySet()) {
                map.put(entry.getKey(), entry.getValue() == null || entry.getValue().length  == 0 ? null : entry.getValue()[0]);
            }
            return map;
        }
    }

    public String getParam(String key) {
        Map<String, Object> paramMap = getParams();
        return paramMap.containsKey(key) ? String.valueOf(paramMap.get(key)) : null;
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public long getContentLengthLong() {
        return body.length;
    }

    @Override
    public int getContentLength() {
        return body.length;
    }

    @Override
    public ServletInputStream getInputStream() {
        if (body == null) {
            body = new byte[0];
        }
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream(){
            @Override
            public int read() {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener arg0) {
            }
        };
    }
}
