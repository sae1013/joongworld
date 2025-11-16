package com.softworks.joongworld.admin.service;

import com.softworks.joongworld.admin.dto.AdminUserView;
import com.softworks.joongworld.admin.repository.AdminUserMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private static final int DEFAULT_LIMIT = 100;

    private final AdminUserMapper adminUserMapper;

    public List<AdminUserView> getRecentUsers() {
        return adminUserMapper.findRecentUsers(DEFAULT_LIMIT);
    }
}
