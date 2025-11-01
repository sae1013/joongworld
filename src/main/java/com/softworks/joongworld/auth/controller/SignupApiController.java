package com.softworks.joongworld.auth.controller;

import com.softworks.joongworld.auth.dto.SignupRequest;
import com.softworks.joongworld.auth.dto.SignupResponse;
import com.softworks.joongworld.auth.service.SignupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class SignupApiController {

    private final SignupService signupService;

    public SignupApiController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = signupService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
