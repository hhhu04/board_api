package com.project.board.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auth {

    /**
     * 인증이 Optional인 API 전용
     *
     * @return
     */
    public boolean isOptional() default false;
}
