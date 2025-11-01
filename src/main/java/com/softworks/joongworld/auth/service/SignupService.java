package com.softworks.joongworld.auth.service;

import com.softworks.joongworld.auth.dto.SignupRequest;
import com.softworks.joongworld.auth.dto.SignupResponse;
import com.softworks.joongworld.user.repository.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SignupService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public SignupService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public SignupResponse register(SignupRequest request) {
        String email = normalizeEmail(request.email());
        String name = normalizeName(request.name());
        String nickname = normalizeNickname(request.nickname());
        String rawPassword = request.password();

        if (userMapper.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }
        if (userMapper.existsByNickname(nickname)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
        }

        String passwordHash = passwordEncoder.encode(rawPassword);
        int inserted = userMapper.insertUser(email, passwordHash, name, nickname);
        if (inserted != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "회원가입 처리 중 오류가 발생했습니다.");
        }

        Long userId = userMapper.findIdByEmail(email);
        return new SignupResponse(userId, email, name, nickname);
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일을 입력해 주세요.");
        }
        String trimmed = email.trim();
        if (trimmed.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일을 입력해 주세요.");
        }
        return trimmed.toLowerCase();
    }

    private String normalizeName(String name) {
        if (name == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이름을 입력해 주세요.");
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이름을 입력해 주세요.");
        }
        return trimmed;
    }

    private String normalizeNickname(String nickname) {
        if (nickname == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임을 입력해 주세요.");
        }
        String trimmed = nickname.trim();
        if (trimmed.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임을 입력해 주세요.");
        }
        return trimmed;
    }
}
