package com.muern.framework.boot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muern.framework.core.common.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author gegeza
 * @date 2020-08-18
 * 配置Spring MVC序列化与反序列化LocalDateTime
 */
@Configuration
public class DateConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateConfig.class);

    /** LocalDate转换器，用于转换RequestParam和PathVariable参数 */
    @Bean
    public Converter<String, LocalDate> localDateConverter() {
        return new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(@NonNull String source) {
                return StringUtils.hasText(source) ?
                        LocalDate.parse(source, DateTimeFormatter.ofPattern(Json.FORMAT_DATE)) : null;
            }
        };
    }

    /** LocalTime转换器，用于转换RequestParam和PathVariable参数 */
    @Bean
    public Converter<String, LocalTime> localTimeConverter() {
        return new Converter<String, LocalTime>() {
            @Override
            public LocalTime convert(@NonNull String source) {
                return StringUtils.hasText(source) ?
                        LocalTime.parse(source, DateTimeFormatter.ofPattern(Json.getDatePattern(source))) : null;
            }
        };
    }

    /** LocalDateTime转换器，用于转换RequestParam和PathVariable参数 */
    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(@NonNull String source) {
                return StringUtils.hasText(source) ?
                    LocalDateTime.parse(source, DateTimeFormatter.ofPattern(Json.getDatePattern(source))) : null;
            }
        };
    }

    /** Date转换器，用于转换RequestParam和PathVariable参数 */
    @Bean
    public Converter<String, Date> dateConverter() {
        return new Converter<String, Date>() {
            @Override
            public Date convert(@NonNull String source) {
                try {
                    return StringUtils.hasText(source) ?
                            new SimpleDateFormat(Json.getDatePattern(source)).parse(source) : null;
                } catch (ParseException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return null;
            }
        };
    }


    /** Json序列化和反序列化转换器，用于转换Post请求体中的json以及将我们的对象序列化为返回响应的json */
    @Bean
    public ObjectMapper objectMapper() {
        return Json.MAPPER;
    }
}
