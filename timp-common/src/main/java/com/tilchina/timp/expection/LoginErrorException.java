package com.tilchina.timp.expection;

/**
 * 登录及认证异常
 * 
 * @version 1.0.0 2018/3/27
 * @author WangShengguang   
 */ 
public class LoginErrorException extends BusinessException {
    public LoginErrorException() {
        super();
    }

    public LoginErrorException(String errorCode, Object... args) {
        super(errorCode, args);
    }

    public LoginErrorException(String errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }

    public LoginErrorException(Throwable cause) {
        super(cause);
    }
}
