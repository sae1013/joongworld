package com.softworks.joongworld.config;

import com.softworks.joongworld.auth.support.CurrentUserArgumentResolver;
import com.softworks.joongworld.notification.support.NotificationCountInterceptor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final CurrentUserArgumentResolver currentUserArgumentResolver;
    private final NotificationCountInterceptor notificationCountInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(notificationCountInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**",
                        "/js/**",
                        "/images/**",
                        "/static/**",
                        "/webjars/**",
                        "/error");
    }
}
