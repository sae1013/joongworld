package com.softworks.joongworld.common.auth;

import com.softworks.joongworld.user.dto.LoginUserInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * {@link RequireLogin} 이 적용된 서비스 메서드 호출 전에 로그인 여부를 검증한다.
 */
@Aspect
@Component
public class RequireLoginAspect {

    @Before("@annotation(com.softworks.joongworld.common.auth.RequireLogin)")
    public void checkLogin(JoinPoint joinPoint) {
        LoginUserInfo user = extractLoginUser(joinPoint.getArgs());
        if (user == null || user.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
    }

    private LoginUserInfo extractLoginUser(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg instanceof LoginUserInfo info) {
                return info;
            }
        }
        throw new IllegalStateException("@RequireLogin 메서드에 LoginUserInfo 파라미터가 없습니다.");
    }
}
