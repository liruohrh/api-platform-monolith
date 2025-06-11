package io.github.liruohrh.apiplatform.controller;

import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user/api")
@RestController
@Validated
public class UserApiController {

  private final UserService userService;

  public UserApiController(
      UserService userService
  ) {
    this.userService = userService;
  }
  @PutMapping("/app-secret")
  public Resp<Void> resetUserAppSecret() {
    userService.resetAppSecret();
    return Resp.ok(null);
  }
}
