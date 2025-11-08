package com.softworks.joongworld.admin.controller.api;

import com.softworks.joongworld.admin.dto.AdminSignupRequest;
import com.softworks.joongworld.admin.dto.AdminSignupResponse;
import com.softworks.joongworld.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminService adminService;

    @PostMapping("/api/admin/signup")
    public ResponseEntity<AdminSignupResponse> signup(@Valid @RequestBody AdminSignupRequest request) {
        AdminSignupResponse response = adminService.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
