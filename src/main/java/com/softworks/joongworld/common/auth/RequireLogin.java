package com.softworks.joongworld.common.auth;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 메서드 레벨에서 로그인 여부를 강제할 때 사용한다.
 * {@link com.softworks.joongworld.common.auth.RequireLoginAspect}가 이 어노테이션을 감지한다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireLogin {
}
