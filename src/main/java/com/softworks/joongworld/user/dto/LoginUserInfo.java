package com.softworks.joongworld.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginUserInfo {
    private Long id;
    private String email;
    private String nickname;
    private boolean admin;
}
