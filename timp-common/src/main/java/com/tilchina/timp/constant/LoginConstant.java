package com.tilchina.timp.constant;

/**
 *
 *
 * @version 1.0.0 2018/4/1
 * @author WangShengguang
 */
public class LoginConstant {

    public static final Integer LOGIN_TYPE_WEB = 0;//"web";
    public static final Integer LOGIN_TYPE_ANDROID = 1;//"android";
    public static final Integer LOGIN_TYPE_IOS = 2;//"ios";
    public static final Integer LOGIN_TYPE_PAD = 3;//"pad";
    public static final Integer LOGIN_TYPE_WX = 4;//"wx";

    public static final String PARAM_CLIENT_TYPE = "clienttype"; //登录类型
    public static final String PARAM_SPECIAL = "tila"; //token加密盐
    public static final String PARAM_TOKEN = "tilb"; // token
    public static final String PRARM_LOGIN_TIME = "tilc"; //登录时间

    public static final String PARAM_DOMAIN = ".";//til-china.com";
}
