package com.softworks.joongworld.user.service;

import com.softworks.joongworld.consts.enums.UserStatus;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserStatusGuard {

  private static final String SUPPORT_EMAIL = "joongo.info@gmail.com";
  private static final String CONTACT_MESSAGE = "운영자 메일 문의: " + SUPPORT_EMAIL;
  private static final String BASE_MESSAGE = "계정 이용이 제한되었어요.";

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
      String reason = user.getSuspensionReasonLabel();
      String message = (reason != null && !reason.isBlank())
          ? BASE_MESSAGE + "\n사유: " + reason + "\n" + CONTACT_MESSAGE
          : BASE_MESSAGE + "\n" + CONTACT_MESSAGE;
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
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
