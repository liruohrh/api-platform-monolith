package io.github.liruohrh.apiplatform.controller;

import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.service.EmailService;
import javax.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/email")
@RestController
@Validated
public class EmailController {
  private final EmailService emailService;

  public EmailController(
      EmailService emailService
  ) {
    this.emailService = emailService;
  }
  @PostMapping("/captcha")
  public Resp<Void> getCaptcha(
      @RequestParam("email")
  @Pattern(regexp = CommonConstant.PATTERN_EMAIL, message = "非法邮箱")
  String email
  ) {
    emailService.captcha(email);
    return Resp.ok(null);
  }
}
