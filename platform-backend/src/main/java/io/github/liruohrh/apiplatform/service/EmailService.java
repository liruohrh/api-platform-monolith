package io.github.liruohrh.apiplatform.service;

import io.github.liruohrh.apiplatform.apicommon.error.BusinessException;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;

public interface EmailService {
  /**
   * 5min内连续请求超过3次，则冻结10min
   * @return captcha
   */
  String captcha(String email);
  /**
   * @param email
   * @param captcha
   * @throws BusinessException 验证码过期
   * @throws ParamException    验证码错误
   */
  void verifyCaptcha(String email, String captcha);
}
