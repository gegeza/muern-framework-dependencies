package com.muern.framework.boot.exception;

import com.muern.framework.core.common.CodeImpl;
import com.muern.framework.core.common.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;

/**
 * @author gegeza
 * @date 2021/08/09
 */
@ControllerAdvice
public class ExceHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    public ExceHandler() {
    }

    @ExceptionHandler({DingException.class})
    @ResponseBody
    public R<Void> dingExceptionHandler(DingException e) {
        LOGGER.error(e.getMessage(), e);
        return R.ins(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({BizException.class})
    @ResponseBody
    public R<Void> bizExceptionHandler(BizException e) {
        LOGGER.error(e.getMessage());
        return R.ins(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = SQLException.class)
    @ResponseBody
    public R<Void> databaseExceptionHandler(SQLException e) {
        LOGGER.error(e.getMessage(), e);
        return R.ins(CodeImpl.ERR_DB);
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public R<Void> exceptionErrorHandler(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return R.ins(CodeImpl.FAIL);
    }
}
