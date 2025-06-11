package io.github.liruohrh.apiplatform.model.req.user;

import io.github.liruohrh.apiplatform.constant.CommonConstant;
import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class UserNewPasswdReq implements Serializable {
  /**
   *
   */
  @Min(value = 5, message = "密码长度不低于5")
  private String newPasswd;
  /**
   *
   */
  @Pattern(regexp = CommonConstant.PATTERN_EMAIL, message = "输入正确邮箱")
  private String email;
  @Size(min = CommonConstant.CAPTCHA_SIZE, max = CommonConstant.CAPTCHA_SIZE)
  private String captcha;
  private static final long serialVersionUID = 1L;
}
