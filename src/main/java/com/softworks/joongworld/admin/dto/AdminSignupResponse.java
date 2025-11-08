package com.softworks.joongworld.admin.dto;

import com.softworks.joongworld.auth.dto.SignupResponse;

/**
 * 어드민 회원가입 응답 DTO
 */
public class AdminSignupResponse extends SignupResponse {

    public AdminSignupResponse() {
        super();
    }

    public AdminSignupResponse(SignupResponse response) {
        super(response.getUserId(),
              response.getEmail(),
              response.getName(),
              response.getNickname(),
              response.isAdmin(),
              response.getPhoneNum(),
              response.getPosition(),
              response.getStatus());
    }

    public static AdminSignupResponse from(SignupResponse response) {
        return new AdminSignupResponse(response);
    }
}
