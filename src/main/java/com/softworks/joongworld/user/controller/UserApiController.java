package com.softworks.joongworld.user.controller;

import com.softworks.joongworld.user.dto.UserResponse;
import com.softworks.joongworld.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserApiController {

  private final UserService userService;

  /**
   * 사용자 조회 API (관리자 > 대시보드 > 유저정보 조회)
   *
   * @param id
   * @return
   */
  @GetMapping("/api/users/{id}")
  public ResponseEntity<UserResponse> getUser(@PathVariable("id") String id) {
    UserResponse user = userService.getUser(id);
    return ResponseEntity.ok(user);
  }
}
