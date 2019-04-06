package com.tilchina.timp.annotation;
/*
 * @author XueYuSong
 * @date 2018-07-26 10:26
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExportField {
	String title() default "";
}
