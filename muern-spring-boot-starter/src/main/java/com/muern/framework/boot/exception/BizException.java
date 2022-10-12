package com.muern.framework.boot.exception;

import com.muern.framework.core.common.Code;
import com.muern.framework.core.common.CodeImpl;
import com.muern.framework.core.common.R;

/**
 * @author gegeza
 * @date 2021/08/09
 */
public class BizException extends RuntimeException{
    protected String code;

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String message) {
        this(CodeImpl.FAIL.getCode(), message);
    }

    public BizException() {
        this(CodeImpl.FAIL);
    }

    public BizException(R<?> result) {
        this(result.getCode(), result.getDesc());
    }

    public BizException(Code code){
        this(code.getCode(), code.getDesc());
    }

    public BizException(String code, String message, Object... args) {
        super(String.format(message, args));
        this.code = code;
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
