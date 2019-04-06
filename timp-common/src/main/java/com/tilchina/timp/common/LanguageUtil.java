package com.tilchina.timp.common;

import com.tilchina.framework.env.LocaleEnvironment;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 *
 *
 * @version 1.0.0 2018/3/29
 * @author WangShengguang
 */
public class LanguageUtil {

    private static MessageSource messageSource;

    private static MessageSource intMessageSource(){
        if(messageSource == null){
            messageSource = new ClassPathXmlApplicationContext("localeConfig.xml");
        }
        return messageSource;
    }

    public static String getMessage(String code, Object... args){
        return getMessage(code, StringUtils.parseLocaleString(LocaleEnvironment.getLocale()),args);
    }

    public static String getMessage(String code, Locale locale, Object... args){
        return getMessage(code,args,locale);
    }

    private static String getMessage(String code, Object[] args, Locale locale){
        return intMessageSource().getMessage(code, args, locale);
    }
}
