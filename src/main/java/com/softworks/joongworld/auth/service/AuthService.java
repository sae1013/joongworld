package com.softworks.joongworld.auth.service;

import com.softworks.joongworld.auth.dto.LoginRequest;
import com.softworks.joongworld.consts.enums.UserStatus;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import com.softworks.joongworld.user.dto.UserAuth;
import com.softworks.joongworld.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * 로그인 서비스
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginUserInfo login(LoginRequest request) {
        // 사용자 인증 정보는 DB를 통해 조회한다.
        UserAuth user = userMapper.findAuthByEmail(request.getEmail());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호를 확인해 주세요.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호를 확인해 주세요.");
        }

        UserStatus status = user.getStatus() == null ? UserStatus.ACTIVE : user.getStatus();
        if (status == UserStatus.PENDING_APPROVAL) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 승인 대기 중입니다. 최고관리자 승인 후 로그인할 수 있습니다.");
        }
        LoginUserInfo userInfo = LoginUserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .admin(user.isAdmin())
                .status(status)
                .reportReasonCode(user.getReportReasonCode())
                .reportReasonDisplayName(user.getReportReasonDisplayName())
                .reportReasonDescription(user.getReportReasonDescription())
                .build();
        return userInfo;
    }
}
