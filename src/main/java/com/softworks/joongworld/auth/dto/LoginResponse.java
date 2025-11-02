package com.softworks.joongworld.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class LoginResponse {
    @JsonIgnore
    private final String accessToken;
    @JsonIgnore
    private final String refreshToken;

    @JsonProperty("token")
    public Map<String, String> token() {
        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }

    private final LoginUserInfo user;
}
