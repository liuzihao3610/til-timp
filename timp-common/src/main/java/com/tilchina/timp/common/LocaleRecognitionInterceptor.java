package com.tilchina.timp.common;

import com.tilchina.framework.env.LocaleEnvironment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.UsesJava7;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 *
 *
 * @version 1.0.0 2018/3/29
 * @author WangShengguang
 */
public class LocaleRecognitionInterceptor extends HandlerInterceptorAdapter {
    public static final String DEFAULT_PARAM_NAME = "locale";
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String paramName = "locale";
    private String[] httpMethods;
    private boolean ignoreInvalidLocale = false;
    private boolean languageTagCompliant = false;

    public LocaleRecognitionInterceptor() {
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return this.paramName;
    }

    public void setHttpMethods(String... httpMethods) {
        this.httpMethods = httpMethods;
    }

    public String[] getHttpMethods() {
        return this.httpMethods;
    }

    public void setIgnoreInvalidLocale(boolean ignoreInvalidLocale) {
        this.ignoreInvalidLocale = ignoreInvalidLocale;
    }

    public boolean isIgnoreInvalidLocale() {
        return this.ignoreInvalidLocale;
    }

    public void setLanguageTagCompliant(boolean languageTagCompliant) {
        this.languageTagCompliant = languageTagCompliant;
    }

    public boolean isLanguageTagCompliant() {
        return this.languageTagCompliant;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        String newLocale = request.getParameter(this.getParamName());

        if(this.checkHttpMethod(request.getMethod())) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if(localeResolver == null) {
                throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
            }

            try {
                if(newLocale != null) {
                    localeResolver.setLocale(request, response, this.parseLocaleValue(newLocale));
                }
                Locale locale = localeResolver.resolveLocale(request);
                LocaleEnvironment.setLocale(locale.toString());
            } catch (IllegalArgumentException var7) {
                if(!this.isIgnoreInvalidLocale()) {
                    throw var7;
                }

                this.logger.debug("Ignoring invalid locale value [" + newLocale + "]: " + var7.getMessage());
            }
        }

        return true;
    }

    private boolean checkHttpMethod(String currentMethod) {
        String[] configuredMethods = this.getHttpMethods();
        if(ObjectUtils.isEmpty(configuredMethods)) {
            return true;
        } else {
            String[] var3 = configuredMethods;
            int var4 = configuredMethods.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String configuredMethod = var3[var5];
                if(configuredMethod.equalsIgnoreCase(currentMethod)) {
                    return true;
                }
            }

            return false;
        }
    }

    @UsesJava7
    protected Locale parseLocaleValue(String locale) {
        return this.isLanguageTagCompliant()?Locale.forLanguageTag(locale): StringUtils.parseLocaleString(locale);
    }
}
