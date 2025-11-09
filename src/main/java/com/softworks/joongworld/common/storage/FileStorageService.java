package com.softworks.joongworld.common.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final StorageProperties storageProperties;

    /**
     * 기본 루트 경로 바로 아래에 파일을 저장한다.
     *
     * @param file 업로드 파일
     * @return 루트 경로 기준 상대 경로 (예: products/uuid.jpg)
     */
    public String store(MultipartFile file) {
        return store(file, null);
    }

    /**
     * 지정한 하위 경로에 파일을 저장한다.
     *
     * @param file      업로드 파일
     * @param subFolder 루트 기준 하위 폴더 (null 또는 공백이면 루트에 저장)
     * @return 루트 경로 기준 상대 경로 (예: products/uuid.jpg)
     */
    public String store(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("업로드된 파일이 비어 있습니다.");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = extractExtension(originalFilename);
        String storedName = UUID.randomUUID().toString().replace("-", "") + extension;

        Path rootPath = storageProperties.rootPath();
        Path targetDir = StringUtils.hasText(subFolder)
                ? rootPath.resolve(subFolder).normalize()
                : rootPath;

        try {
            Files.createDirectories(targetDir);
        } catch (IOException e) {
            throw new StorageException("파일 저장 디렉터리를 생성할 수 없습니다: " + targetDir, e);
        }

        Path destination = targetDir.resolve(storedName).normalize();
        if (!destination.startsWith(rootPath)) {
            throw new StorageException("잘못된 저장 경로가 감지되었습니다.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("파일 저장에 실패했습니다: " + originalFilename, e);
        }

        Path relativePath = rootPath.relativize(destination);
        return relativePath.toString().replace("\\", "/");
    }

    /**
     * 저장된 상대 경로를 실제 파일 시스템 경로로 변환한다.
     */
    public Path load(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            throw new StorageException("상대 경로가 비어 있습니다.");
        }
        Path path = storageProperties.rootPath().resolve(relativePath).normalize();
        if (!path.startsWith(storageProperties.rootPath())) {
            throw new StorageException("잘못된 파일 접근 경로입니다.");
        }
        return path;
    }

    /**
     * 저장된 파일을 삭제한다.
     *
     * @param relativePath 루트 기준 상대 경로
     */
    public void delete(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return;
        }
        Path target;
        try {
            target = load(relativePath);
        } catch (StorageException ex) {
            log.warn("삭제할 파일 경로를 확인할 수 없습니다. path={}", relativePath, ex);
            return;
        }

        try {
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            log.warn("파일 삭제에 실패했습니다. path={}", target, ex);
        }
    }

    private String extractExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex);
    }
}
