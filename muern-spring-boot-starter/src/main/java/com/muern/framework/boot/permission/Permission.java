package com.muern.framework.boot.permission;

import java.lang.annotation.*;

/**
 * 校验接口权限
 * @author 272215
 * @date 2022/09/06
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permission {

    /**
     * 权限code名称
     */
    String[] code() default {};
}
