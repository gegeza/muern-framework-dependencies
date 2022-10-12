package com.muern.framework.boot.permission;

import com.muern.framework.boot.exception.DingException;
import com.muern.framework.core.common.CodeImpl;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author gegeza
 * @date 2022/09/06
 */
@Aspect
@Component
@ConditionalOnBean(PermissionVerify.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
public class PermissionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionAdvice.class);

    @PostConstruct
    void start() {
        LOGGER.info("Enable PermissionAdvice Successful");
    }

    @Resource
    private PermissionVerify permissionVerify;

    @Before("@annotation(permission)")
    public void permissionCheckFirst(Permission permission) {
        if (!permissionVerify.verify(permission.code())) {
            LOGGER.error("========权限校验失败========");
            throw new DingException(CodeImpl.ERR_PERMISSION);
        }
    }
}
