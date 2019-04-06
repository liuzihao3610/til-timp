package com.tilchina.timp.annotation;
/*
 * @author XueYuSong
 * @date 2018-07-24 10:11
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ImportField {
	String title() default "";
}
