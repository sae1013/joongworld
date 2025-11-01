package com.softworks.joongworld.user.service;

import com.softworks.joongworld.user.dto.UserResponse;
import com.softworks.joongworld.user.repository.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserResponse getUser(String id) {
        String normalized = normalize(id);

        UserResponse user = tryFindById(normalized);
        if (user == null && normalized.contains("@")) {
            user = userMapper.findByEmail(normalized);
        }
        if (user == null) {
            user = userMapper.findByNickname(normalized);
        }

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        return user;
    }

    private String normalize(String id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "검색할 ID를 입력해 주세요.");
        }
        String trimmed = id.trim();
        if (trimmed.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "검색할 ID를 입력해 주세요.");
        }
        return trimmed;
    }

    private UserResponse tryFindById(String id) {
        try {
            Long asLong = Long.parseLong(id);
            return userMapper.findById(asLong);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
