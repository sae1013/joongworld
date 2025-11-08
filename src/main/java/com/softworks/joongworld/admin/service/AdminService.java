package com.softworks.joongworld.admin.service;

import com.softworks.joongworld.admin.dto.AdminSignupRequest;
import com.softworks.joongworld.admin.dto.AdminSignupResponse;
import com.softworks.joongworld.auth.dto.SignupResponse;
import com.softworks.joongworld.auth.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final SignupService signupService;

    public AdminSignupResponse registerAdmin(AdminSignupRequest request) {
        SignupResponse response = signupService.register(request);
        return AdminSignupResponse.from(response);
    }
}
