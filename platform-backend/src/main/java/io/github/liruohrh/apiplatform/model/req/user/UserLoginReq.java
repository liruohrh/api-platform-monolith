package io.github.liruohrh.apiplatform.model.req.user;

import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.enume.LoginType;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginReq implements Serializable {
  @NotNull(message = "必须传递客户端登录方式参数")
  private LoginType loginType;
  /**
   *
   */
  @Pattern(regexp = CommonConstant.PATTERN_USERNAME, message = "用户名仅包含字母数字下划线")
  private String username;

  /**
   *
   */
  @Size(min = 5, message = "密码长度不低于5")
  private String passwd;
  /**
   *
   */
  @Pattern(regexp = CommonConstant.PATTERN_EMAIL, message = "输入正确邮箱")
  private String email;

  @Size(min = CommonConstant.CAPTCHA_SIZE, max = CommonConstant.CAPTCHA_SIZE, message = "验证码长度只有6")
  private String captcha;

  public User toUserEntity() {
    User user = new User();
    user.setUsername(username);
    user.setPasswd(passwd);
    user.setEmail(email);
    return user;
  }
  private static final long serialVersionUID = 1L;
}
