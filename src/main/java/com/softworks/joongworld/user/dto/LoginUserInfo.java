package com.softworks.joongworld.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String reportReasonCode;
    private String reportReasonDisplayName;
    private String reportReasonDescription;

    public static LoginUserInfo empty() {
        return LoginUserInfo.builder()
                .id(null)
                .email(null)
                .nickname(null)
                .admin(false)
                .status(UserStatus.ACTIVE)
                .reportReasonCode(null)
                .reportReasonDisplayName(null)
                .reportReasonDescription(null)
                .build();
    }

    @JsonIgnore
    public String getSuspensionReasonLabel() {
        if (reportReasonDisplayName != null && !reportReasonDisplayName.isBlank()) {
            return reportReasonDisplayName;
        }
        if (reportReasonDescription != null && !reportReasonDescription.isBlank()) {
            return reportReasonDescription;
        }
        return null;
    }
}
