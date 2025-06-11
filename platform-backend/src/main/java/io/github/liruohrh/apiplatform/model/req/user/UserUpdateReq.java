package io.github.liruohrh.apiplatform.model.req.user;

import io.github.liruohrh.apiplatform.constant.CommonConstant;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateReq implements Serializable {

  @NotNull(message = "不知道更新哪个用户")
  private Long id;
  /**
   *
   */
  @Size(min = 1, message = "头像不能是空字符串")
  private String avatarUrl;
  /**
   *
   */
  @Size(min = 1, message = "昵称不能是空字符串")
  private String nickname;

  /**
   *
   */
  @Pattern(regexp = CommonConstant.PATTERN_USERNAME, message = "用户名仅包含字母数字下划线")
  private String username;
  @Size(max = 256, message = "个人描述长度最多256")
  private String personalDescription;


  //admin修改不需要captcha
  @Pattern(regexp = CommonConstant.PATTERN_EMAIL, message = "输入正确邮箱")
  private String email;
  @Size(min = CommonConstant.CAPTCHA_SIZE, max = CommonConstant.CAPTCHA_SIZE)
  private String captcha;

  //admin才能修改
  private String role;
  private Integer status;
  private static final long serialVersionUID = 1L;

}
