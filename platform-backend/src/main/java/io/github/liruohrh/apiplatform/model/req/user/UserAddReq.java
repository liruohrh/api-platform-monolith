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
public class UserAddReq implements Serializable {

  /**
   *
   */
  @Size(min = 1, max = 30, message = "昵称长度为1-30")
  private String nickname;

  /**
   *
   */
  @NotEmpty(message = "用户名不能为空")
  @Size(min = 1, max = 30, message = "用户名长度为1-30")
  @Pattern(regexp = CommonConstant.PATTERN_USERNAME, message = "用户名仅包含字母数字下划线")
  private String username;

  /**
   *
   */
  @NotEmpty(message = "密码不能为空")
  @Min(value = 5, message = "密码长度至少为5")
  private String passwd;
  /**
   *
   */
  @NotEmpty(message = "邮箱号不能为空")
  @Pattern(regexp = CommonConstant.PATTERN_EMAIL, message = "输入正确邮箱")
  private String email;

  @NotEmpty(message = "角色不能为空")
  private String role;
  public User toUserEntity() {
    User user = new User();
    user.setNickname(nickname);
    user.setUsername(username);
    user.setPasswd(passwd);
    user.setEmail(email);
    user.setRole(role);
    return user;
  }
  private static final long serialVersionUID = 1L;
}
