package com.softworks.joongworld.auth.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.softworks.joongworld.consts.enums.AdminPosition;
import com.softworks.joongworld.consts.enums.UserStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponse {

    private Long userId;
    private String email;
    private String name;
    private String nickname;
    private boolean isAdmin;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private String phoneNum = "";

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private AdminPosition position;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private UserStatus status = UserStatus.ACTIVE;

    @JsonGetter("position")
    public String getPositionValue() {
        return position == null ? "" : position.getDisplayName();
    }

    @JsonSetter("position")
    public void setPositionValue(String value) {
        this.position = AdminPosition.from(value);
    }

    @JsonIgnore
    public AdminPosition getPosition() {
        return position;
    }

    public void setPosition(AdminPosition position) {
        this.position = position;
    }

    @JsonGetter("status")
    public String getStatusValue() {
        return status == null ? UserStatus.ACTIVE.name() : status.name();
    }

    @JsonSetter("status")
    public void setStatusValue(String value) {
        this.status = UserStatus.from(value);
    }

    @JsonIgnore
    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status == null ? UserStatus.ACTIVE : status;
    }
}
