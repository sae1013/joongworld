package com.softworks.joongworld.user.dto;

import com.softworks.joongworld.consts.enums.UserStatus;
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
    private UserStatus status;

    public static LoginUserInfo empty() {
        return LoginUserInfo.builder()
                .id(null)
                .email(null)
                .nickname(null)
                .admin(false)
                .status(UserStatus.ACTIVE)
                .build();
    }
}
