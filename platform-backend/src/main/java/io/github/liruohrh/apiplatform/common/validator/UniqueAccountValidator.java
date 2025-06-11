package io.github.liruohrh.apiplatform.common.validator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.liruohrh.apiplatform.common.util.ValidatorUtils;
import io.github.liruohrh.apiplatform.model.req.user.UserRegisterReq;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.service.UserService;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueAccountValidator implements ConstraintValidator<UniqueAccount, UserRegisterReq> {

  private final UserService userService;

  public UniqueAccountValidator(UserService userService) {
    this.userService = userService;
  }

  @Override
  public boolean isValid(UserRegisterReq value, ConstraintValidatorContext context) {
    boolean emailExists = userService.exists(new LambdaQueryWrapper<User>()
        .eq(User::getEmail, value.getEmail()));
    if(emailExists){
      ValidatorUtils.addErrorMessage(context, "邮箱已存在");
      return false;
    }
    boolean usernameExists = userService.exists(new LambdaQueryWrapper<User>()
        .eq(User::getUsername, value.getUsername()));
    if(usernameExists){
      ValidatorUtils.addErrorMessage(context, "用户名已存在");
      return false;
    }
    return true;
  }
}
