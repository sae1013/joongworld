package com.softworks.joongworld.user.dto;

import com.softworks.joongworld.consts.enums.UserStatus;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private String nickname;
    private String phoneNum;
    private String position;
    private boolean isAdmin;
    private UserStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
