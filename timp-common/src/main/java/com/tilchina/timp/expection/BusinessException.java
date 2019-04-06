package com.tilchina.timp.expection;

import com.tilchina.catalyst.exception.BaseException;
import com.tilchina.timp.common.LanguageUtil;

/**
 *
 *
 * @version 1.0.0 2018/3/29
 * @author WangShengguang
 */
public class BusinessException extends BaseException {
    public BusinessException() {
        super();
    }

    public BusinessException(String errorCode,Object... args) {
        super(errorCode,LanguageUtil.getMessage(errorCode,args));
    }

    public BusinessException(String errorCode, Throwable cause, Object... args ) {
        super(errorCode, LanguageUtil.getMessage(errorCode,args), cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }
}
