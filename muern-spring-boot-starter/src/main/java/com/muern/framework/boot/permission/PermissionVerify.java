package com.muern.framework.boot.permission;

/**
 * 权限校验接口
 * @author gegeza
 * @date 2022/09/06
 */
public interface PermissionVerify {
    boolean verify(String[] code);
}
