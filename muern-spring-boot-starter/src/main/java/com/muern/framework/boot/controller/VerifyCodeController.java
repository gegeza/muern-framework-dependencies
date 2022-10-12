package com.muern.framework.boot.controller;

import com.muern.framework.core.utils.VerifyCodeUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.muern.framework.boot.redis.RedisKey.VERIFY_CODE_KEY;


/**
 * 图形验证码
 * @author gegeza
 * @date 2022/09/08
 */
@Controller
@RequestMapping("verifycode")
@ConditionalOnProperty(prefix = "muern.verifycode", name = "enable", havingValue = "true")
public class VerifyCodeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyCodeController.class);

    /** 验证码长度 默认为4位 */
    @Value("${muern.verifycode.str-length:4}") private Integer length;

    /** 验证码源字符串 字体只显示大写，去掉了1,0,i,o几个容易混淆的字符 */
    @Value("${muern.verifycode.str-codes:23456789ABCDEFGHJKLMNPQRSTUVWXYZ}") private String sources;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 返回图形验证码
     * @param response 响应代码
     */
    @GetMapping("image")
    public void image(HttpServletResponse response) {
        try {
            String verifyCode = VerifyCodeUtils.generateVerifyCode(sources, length), verifyCodeKey = UUID.randomUUID().toString();
            response.addHeader("verifyTicket", verifyCodeKey);
            response.setContentType(ContentType.IMAGE_JPEG.toString());
            stringRedisTemplate.opsForValue().set(VERIFY_CODE_KEY.concat(verifyCodeKey), verifyCode, 300, TimeUnit.SECONDS);
            VerifyCodeUtils.outputImage(250, 100, response.getOutputStream(), verifyCode);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
