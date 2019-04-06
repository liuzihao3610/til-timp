package com.tilchina.timp.annotation;
import java.lang.annotation.*;

/** 
 * 
 * 是否需要从解析excel赋值 
 * @author  
 * @see [相关类/方法] 
 * @since [产品/模块版本] 
 */
@Retention(value = RetentionPolicy.RUNTIME) 
@Target(value = {ElementType.FIELD})
@Inherited
public @interface IsNeeded 
{ 
   
 /** 
  * 是否需要从解析excel赋值 
  * @return 
  *   true:需要 false:不需要 
  * @see [类、类#方法、类#成员] 
  */
 boolean isNeeded() default true;
}
