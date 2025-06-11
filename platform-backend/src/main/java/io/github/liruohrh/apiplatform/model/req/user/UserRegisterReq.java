package io.github.liruohrh.apiplatform.model.req.user;

import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.model.entity.User;
import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterReq implements Serializable {

  /**
   *
   */
  @NotEmpty(message = "昵称不能为空")
  private String nickname;

  /**
   *
   */
  @Pattern(regexp = CommonConstant.PATTERN_USERNAME, message = "用户名仅包含字母数字下划线")
  private String username;

  /**
   *
   */
  @Min(value = 5, message = "密码长度至少为5")
  private String passwd;
  /**
   *
   */
  @Pattern(regexp = CommonConstant.PATTERN_EMAIL, message = "输入正确邮箱")
  private String email;
  @Size(min = CommonConstant.CAPTCHA_SIZE, max = CommonConstant.CAPTCHA_SIZE)
  private String captcha;

  public User toUserEntity() {
    User user = new User();
    user.setNickname(nickname);
    user.setUsername(username);
    user.setPasswd(passwd);
    user.setEmail(email);
    return user;
  }
  private static final long serialVersionUID = 1L;
}
