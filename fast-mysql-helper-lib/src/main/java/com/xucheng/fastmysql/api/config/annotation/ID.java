package com.xucheng.fastmysql.api.config.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ID {
    boolean autoIncrement() default true;
}
