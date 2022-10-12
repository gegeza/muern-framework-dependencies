package com.muern.framework.boot.filter;

import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author gegeza
 * @date 2019-11-06 4:56 PM
 */
public class CachingContentResponseWrapper extends HttpServletResponseWrapper {
    private ServletOutputStream output;
    private ByteArrayOutputStream byteArrayOutputStream;

    public CachingContentResponseWrapper(HttpServletResponse response) {
        super(response);
        try {
            output = response.getOutputStream();
            byteArrayOutputStream = new ByteArrayOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PrintWriter getWriter() {
        return new PrintWriter(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                output.write(b);
                byteArrayOutputStream.write(b);
            }
        });
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener listener) {

            }

            @Override
            public void write(int b) throws IOException {
                output.write(b);
                byteArrayOutputStream.write(b);
            }
        };
    }

    public String getResponseContent() {
        String content;
        if (StringUtils.hasText(getContentType())) {
            if (getContentType().startsWith("application/json")) {
                content = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
            } else {
                content = "Unsupport Content-Type: " + getContentType();
            }
        } else {
            content = "Response Content-Type is empty";
        }
        return content;
    }
}
