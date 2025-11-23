package com.softworks.joongworld.admin.service;

import com.softworks.joongworld.admin.dto.AdminUserView;
import com.softworks.joongworld.admin.repository.AdminUserMapper;
import com.softworks.joongworld.consts.enums.UserStatus;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import com.softworks.joongworld.user.dto.UserResponse;
import com.softworks.joongworld.user.repository.UserMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private static final int DEFAULT_LIMIT = 100;

    private final AdminUserMapper adminUserMapper;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<AdminUserView> getRecentUsers() {
        return adminUserMapper.findRecentUsers(DEFAULT_LIMIT);
    }

    @Transactional(readOnly = true)
    public List<AdminUserView> getPendingApprovals() {
        return adminUserMapper.findPendingApprovals(DEFAULT_LIMIT);
    }

    @Transactional
    public void approvePendingManager(Long userId, LoginUserInfo currentUser) {
        ensureAdmin(currentUser);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "승인할 사용자를 선택해 주세요.");
        }
        UserResponse user = userMapper.findById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        UserStatus status = user.getStatus() == null ? UserStatus.ACTIVE : user.getStatus();
        if (status != UserStatus.PENDING_APPROVAL) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 처리된 요청입니다.");
        }
        int updated = adminUserMapper.approvePendingUser(userId);
        if (updated != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "승인 처리에 실패했습니다.");
        }
    }

    @Transactional
    public void rejectPendingManager(Long userId, LoginUserInfo currentUser) {
        ensureAdmin(currentUser);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "거절할 사용자를 선택해 주세요.");
        }
        UserResponse user = userMapper.findById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        UserStatus status = user.getStatus() == null ? UserStatus.ACTIVE : user.getStatus();
        if (status != UserStatus.PENDING_APPROVAL) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 처리된 요청입니다.");
        }
        int updated = adminUserMapper.rejectPendingUser(userId);
        if (updated != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "거절 처리에 실패했습니다.");
        }
    }

    private LoginUserInfo ensureAdmin(LoginUserInfo currentUser) {
        if (currentUser == null || currentUser.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (!currentUser.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다.");
        }
        return currentUser;
    }
}
