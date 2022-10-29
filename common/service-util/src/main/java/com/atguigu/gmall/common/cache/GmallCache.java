package com.atguigu.gmall.common.cache;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GmallCache {

    // 缓存的前缀
    String prefix() default "cache:";

    // 缓存的后缀
    String suffix() default ":info";
}
