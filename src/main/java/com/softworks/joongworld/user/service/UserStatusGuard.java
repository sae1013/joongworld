package com.softworks.joongworld.user.service;

import com.softworks.joongworld.consts.enums.UserStatus;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserStatusGuard {

  /**
   * 유저가 글을 쓸 수 있는 상태인지 판별하는 Guard
   *
   * @param user
   */
  public void ensureCanSell(LoginUserInfo user) {
    if (user == null || user.getId() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }
    if (isSuspended(user)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "정지된 계정입니다. 운영자에게 문의해 주세요.");
    }
  }

  /**
   * 현재 유저가 정지 인경우 글쓰기 불가능
   *
   * @param user
   * @return
   */
  public boolean isSuspended(LoginUserInfo user) {
    return user != null && user.getStatus() == UserStatus.SUSPENDED;
  }
}
