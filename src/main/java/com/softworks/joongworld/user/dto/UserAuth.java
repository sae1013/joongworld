package com.softworks.joongworld.user.dto;

import com.softworks.joongworld.consts.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuth {
    private Long id;
    private String email;
    private String passwordHash;
    private String name;
    private String nickname;
    private boolean isAdmin;
    private String phoneNum;
    private String position;
    private UserStatus status;
}
