package io.github.liruohrh.apiplatform.common.validator;

import cn.hutool.core.util.StrUtil;
import io.github.liruohrh.apiplatform.common.util.ValidatorUtils;
import io.github.liruohrh.apiplatform.model.req.user.UserLoginReq;
import io.github.liruohrh.apiplatform.model.enume.LoginType;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LoginParamCheckValidator implements ConstraintValidator<LoginParamCheck, UserLoginReq> {


  @Override
  public boolean isValid(UserLoginReq value, ConstraintValidatorContext context) {
     if (value.getLoginType() == LoginType.CODE && (
         StrUtil.isEmpty(value.getCaptcha()) || StrUtil.isEmpty(
             value.getEmail())
         )) {
      ValidatorUtils.addErrorMessage(context, "请输入正确的邮件验证码登录参数");
      return false;
    } else if (value.getLoginType() == LoginType.PASSWD && (
         StrUtil.isEmpty(value.getPasswd()) || (
             StrUtil.isEmpty(value.getUsername()) && StrUtil.isEmpty(value.getEmail())
         )
         )) {
      ValidatorUtils.addErrorMessage(context, "请输入正确的密码登录参数");
      return false;
    }
    return true;
  }
}
