package com.softworks.joongworld.auth.support;

import com.softworks.joongworld.user.dto.LoginUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * {@link CurrentUser} 가 붙은 컨트롤러 파라미터에 {@link LoginUserInfo} 를 주입한다.
 * 세션 필터가 저장한 사용자 정보를 우선 사용하고, 없으면 시큐리티 컨텍스트를 fallback 으로 사용한다.
 */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
            && LoginUserInfo.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request != null) {
            Object attr = request.getAttribute(SessionAuthenticationFilter.CURRENT_USER_ATTR);
            if (attr instanceof LoginUserInfo info) {
                return info;
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUserInfo info) {
            return info;
        }

        return LoginUserInfo.empty();
    }
}
