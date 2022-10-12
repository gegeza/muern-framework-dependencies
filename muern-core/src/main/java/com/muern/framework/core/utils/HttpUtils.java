package com.muern.framework.core.utils;

import com.muern.framework.core.common.Json;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gegeza
 * @date 2021/08/23
 */
public final class HttpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);
    private static final RestTemplate HTTP_TEMPLATE = new RestTemplate();
    private static final RestTemplate HTTPS_TEMPLATE;

    static {
        //处理HTTP请求的中文乱码问题
        HTTP_TEMPLATE.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        //构造HTTPS请求
        TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            LOGGER.error(e.getMessage(), e);
        }
        SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setSSLSocketFactory(connectionSocketFactory);
        CloseableHttpClient httpClient = httpClientBuilder.build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        HTTPS_TEMPLATE = new RestTemplate(factory);
        //设置HTTPS中文乱码问题
        HTTPS_TEMPLATE.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public static String get(String url) {
        return get(url, String.class);
    }

    public static <T> T get(String url, Class<T> responseType) {
        return send(url, HttpMethod.GET, MediaType.APPLICATION_FORM_URLENCODED, null, null, responseType);
    }

    public static String get(String url, Map<String, ?> params, Map<String, String> headers) {
        return send(url, HttpMethod.GET, MediaType.APPLICATION_FORM_URLENCODED, params, headers, String.class);
    }

    public static <T> T get(String url, Map<String, ?> params, Map<String, String> headers, Class<T> responseType) {
        return send(url, HttpMethod.GET, MediaType.APPLICATION_FORM_URLENCODED, params, headers, responseType);
    }

    public static String post(String url) {
        return post(url, String.class);
    }

    public static <T> T post(String url, Class<T> responseType) {
        return send(url, HttpMethod.POST, MediaType.APPLICATION_FORM_URLENCODED, null, null, responseType);
    }

    public static String post(String url, Map<String, ?> params, Map<String, String> headers) {
        return send(url, HttpMethod.POST, MediaType.APPLICATION_FORM_URLENCODED, params, headers, String.class);
    }

    public static String postJson(String url) {
        return postJson(url, null, null);
    }

    public static String postJson(String url, Map<String, ?> params, Map<String, String> headers) {
        return send(url, HttpMethod.POST, MediaType.APPLICATION_JSON, params, headers, String.class);
    }

    private static <T> T send(String url, HttpMethod httpMethod, MediaType mediaType, Map<String, ?> params, Map<String, String> headers, Class<T> responseType) {
        if (mediaType.equalsTypeAndSubtype(MediaType.APPLICATION_FORM_URLENCODED) && params !=null && !params.isEmpty()) {
            String paramUri = params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
            url += url.contains("?") ? "&" + paramUri : "?" + paramUri;
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(mediaType);
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(httpHeaders::add);
        }
        String body = mediaType.equalsTypeAndSubtype(MediaType.APPLICATION_FORM_URLENCODED) ? null : Json.tostr(params);
        HttpEntity<String> requestEntity = new HttpEntity<>(body, httpHeaders);
        RestTemplate restTemplate = url.startsWith("http:") ? HTTP_TEMPLATE : HTTPS_TEMPLATE;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("HTTP_URL ==> {}", url);
            LOGGER.debug("HTTP_BODY ==> {}", body);
            LOGGER.debug("HTTP_HEADERS ==> {}", Json.tostr(httpHeaders));
        }
        T t = restTemplate.exchange(url, httpMethod, requestEntity, responseType).getBody();
        if (responseType.equals(String.class)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("HTTP_RECI ==> {}", t);
            }
        }
        return t;
    }
}
