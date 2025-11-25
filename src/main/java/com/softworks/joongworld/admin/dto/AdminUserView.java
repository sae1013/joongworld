package com.softworks.joongworld.admin.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserView {
    private Long id;
    private String name;
    private String email;
    private String nickname;
    private String role;
    private String status;
    private OffsetDateTime joinedAt;
}
