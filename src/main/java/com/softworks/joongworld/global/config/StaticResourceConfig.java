package com.softworks.joongworld.global.config;

import com.softworks.joongworld.global.storage.StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
/**
 * 로컬 이미지 파일 경로에 접근하기 위한 세팅
 */
public class StaticResourceConfig implements WebMvcConfigurer {

    private final StorageProperties storageProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = storageProperties.rootPath().toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }

        if (!registry.hasMappingForPattern("/files/**")) {
            registry.addResourceHandler("/files/**")
                    .addResourceLocations(location)
                    .setCacheControl(CacheControl.maxAge(Duration.ofDays(7)).cachePublic());
        }
    }
}
