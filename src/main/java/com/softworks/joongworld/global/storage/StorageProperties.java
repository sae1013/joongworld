package com.softworks.joongworld.global.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    /**
     * 루트 저장 경로. application.properties에서 app.storage.root 로 설정.
     */
    private String root = "uploads";

    public Path rootPath() {
        return Paths.get(root).toAbsolutePath().normalize();
    }
}
