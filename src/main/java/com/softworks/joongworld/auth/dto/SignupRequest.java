package com.softworks.joongworld.auth.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.softworks.joongworld.consts.enums.AdminPosition;
import com.softworks.joongworld.consts.enums.UserStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email(message = "올바른 이메일 형식을 입력해 주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하로 입력해 주세요.")
    private String password;

    @NotBlank(message = "이름을 입력해 주세요.")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
    private String name;

    @NotBlank(message = "닉네임을 입력해 주세요.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,15}$", message = "닉네임은 2~15자의 한글, 영문 또는 숫자여야 합니다.")
    private String nickname;

    @AssertTrue(message = "이용 약관에 동의해 주세요.")
    private boolean agree;

    private Boolean isAdmin;

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
