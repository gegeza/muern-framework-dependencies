package com.muern.framework.boot.exception;

import com.muern.framework.core.common.Code;
import com.muern.framework.core.common.CodeImpl;
import com.muern.framework.core.common.R;

/**
 * @author gegeza
 * @date 2022/09/02
 */
public class DingException extends RuntimeException{
    protected String code;

    public DingException(String code, String message) {
        super(message);
        this.code = code;
    }

    public DingException(String message) {
        this(CodeImpl.FAIL.getCode(), message);
    }

    public DingException() {
        this(CodeImpl.FAIL);
    }

    public DingException(R<?> result) {
        this(result.getCode(), result.getDesc());
    }

    public DingException(Code code){
        this(code.getCode(), code.getDesc());
    }

    public DingException(String code, String message, Object... args) {
        super(String.format(message, args));
        this.code = code;
    }

    public DingException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
