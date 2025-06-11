package io.github.liruohrh.apiplatform.controller;

import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.apicommon.utils.ServletUtils;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.common.util.LoginUtils;
import io.github.liruohrh.apiplatform.common.validator.LoginParamCheck;
import io.github.liruohrh.apiplatform.common.validator.UniqueAccount;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.req.user.UserLoginReq;
import io.github.liruohrh.apiplatform.model.req.user.UserNewPasswdReq;
import io.github.liruohrh.apiplatform.model.req.user.UserRegisterReq;
import io.github.liruohrh.apiplatform.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@Validated
public class AuthController {
  private final UserService userService;

  public AuthController(
      UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/logout")
  public Resp<Void> logout() {
    LoginUtils.clearLoginState();
    return Resp.ok(null);
  }

  @PostMapping("/register")
  public Resp<Void> register(
      @Validated @UniqueAccount @RequestBody UserRegisterReq userRegisterReq) {
    userService.register(userRegisterReq);
    return Resp.ok(null);
  }

  @PostMapping("/login")
  public Resp<User> login(@Validated @LoginParamCheck @RequestBody UserLoginReq userLoginReq) {
    if (LoginUserHolder.isLogin()) {
      throw new ParamException("不允许多次登录");
    }
    return Resp.ok(userService.login(userLoginReq));
  }

  @GetMapping
  public Resp<User> getLoginUser() {
    return Resp.ok(LoginUserHolder.get());
  }

  @PostMapping("/passwd")
  public Resp<Void> newUserPasswd(@RequestBody @Validated UserNewPasswdReq userNewPasswdReq) {
    userService.newPasswd(userNewPasswdReq);
    HttpServletRequest req = ServletUtils.getRequest();
    HttpSession session = req.getSession(false);
    if(session != null) {
      session.invalidate();
    }
    return Resp.ok(null);
  }
}
