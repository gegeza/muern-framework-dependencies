package com.muern.framework.core.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.muern.framework.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author gegeza
 * @date 2022/08/17
 */
public final class Json {

    private static final Logger LOGGER = LoggerFactory.getLogger(Json.class);

    public static final ObjectMapper MAPPER = new ObjectMapper();
    /** 默认日期格式 */
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    /** 默认时间格式 */
    public static final String FORMAT_TI = "HH:mm";
    public static final String FORMAT_TIME = "HH:mm:ss";
    /** 默认日期时间格式 */
    public static final String FORMAT_DATETI = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";

    static {
        //忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        //序列化时对象的所有字段
        MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //处理时间格式数据
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class,new LocalDateSerializer(DateTimeFormatter.ofPattern(FORMAT_DATE)));
        module.addSerializer(LocalTime.class,new LocalTimeSerializer(DateTimeFormatter.ofPattern(FORMAT_TIME)));
        module.addSerializer(LocalDateTime.class,new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(FORMAT_DATETIME)));
        module.addDeserializer(LocalDate.class,new LocalDateDeserializer(DateTimeFormatter.ofPattern(FORMAT_DATE)));
        module.addDeserializer(LocalTime.class,new JsonDeserializer<LocalTime>() {
            @Override
            public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                String dateStr = jsonParser.getText();
                return LocalTime.parse(dateStr, DateTimeFormatter.ofPattern(getDatePattern(dateStr)));
            }
        });
        module.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                String dateStr = jsonParser.getText();
                return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(getDatePattern(dateStr)));
            }
        });
        module.addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat(FORMAT_DATETIME)));
        module.addDeserializer(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                String dateStr = jsonParser.getText();
                try {
                    return new SimpleDateFormat(getDatePattern(dateStr)).parse(dateStr);
                } catch (ParseException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return null;
            }
        });
        MAPPER.registerModule(module);
    }

    private Json() {}

    /** 根据日期字符串获取解析格式 */
    public static String getDatePattern(String text) {
        boolean vailText = StringUtils.hasText(text) && (text.length() == FORMAT_DATETIME.length() || text.length() == FORMAT_TIME.length()
                || text.length() == FORMAT_DATETI.length() || text.length() == FORMAT_TI.length() || text.length() == FORMAT_DATE.length());
        if (vailText) {
            return FORMAT_DATETIME.length() == text.length() ? FORMAT_DATETIME : FORMAT_TIME.length() == text.length() ? FORMAT_TIME
                    : FORMAT_DATETI.length() == text.length() ? FORMAT_DATETI : FORMAT_TI.length() == text.length() ? FORMAT_TI : FORMAT_DATE;
        }
        throw new RuntimeException(text + "can not pase!");
    }

    /**
     * 将Java对象转化为JSON字符串
     * @param object java对象
     * @return java.lang.String
     */
    public static String tostr(Object object) {
        try {
            return object == null ? null : object instanceof String ? object.toString() : MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将JSON字符串转化为Java对象
     * @param str JSON字符串
     * @param clazz Java对象Class
     * @return T 转换后的对象
     */
    public static <T> T parse(String str, Class<T> clazz) {
        try {
            return !StringUtils.hasLength(str) || clazz == null ? null : MAPPER.readValue(str, clazz);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将JSON字符串转化为Java泛型对象
     * @param str JSON字符串
     * @param typeReference Java泛型对象
     * @return T 转换后的对象
     */
    public static <T> T parse(String str, TypeReference<T> typeReference) {
        try {
            return !StringUtils.hasLength(str) || typeReference == null ? null : MAPPER.readValue(str, typeReference);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将Java对象转换成JsonNode对象
     * @param obj Java对象
     * @return com.fasterxml.jackson.databind.JsonNode
     */
    public static JsonNode parseNode(Object obj) {
        return obj == null ? null : parseNode(Json.tostr(obj));
    }

    /**
     * 将Java字符串转换成JsonNode对象
     * @param str Java字符串
     * @return com.fasterxml.jackson.databind.JsonNode
     */
    public static JsonNode parseNode(String str) {
        try {
            return !StringUtils.hasLength(str) ? null : MAPPER.readTree(str);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}
