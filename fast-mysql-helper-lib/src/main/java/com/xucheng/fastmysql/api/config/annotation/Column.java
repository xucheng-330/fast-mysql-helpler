package com.xucheng.fastmysql.api.config.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Column {
    String value() default "";
    int length() default -1;
    int decimalLength() default 2;
}
