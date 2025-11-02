package com.softworks.joongworld.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class UserInfoView {

    private final Long id;
    private final String name;
    private final String nickname;
    private final Boolean isAdmin;
}
