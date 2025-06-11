package io.github.liruohrh.apiplatform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.liruohrh.apiplatform.apicommon.error.BusinessException;
import io.github.liruohrh.apiplatform.apicommon.error.ErrorCode;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.common.util.LoginUtils;
import io.github.liruohrh.apiplatform.common.util.MustUtils;
import io.github.liruohrh.apiplatform.common.util.SecurityUtils;
import io.github.liruohrh.apiplatform.mapper.UserMapper;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.enume.LoginType;
import io.github.liruohrh.apiplatform.model.enume.RoleEnum;
import io.github.liruohrh.apiplatform.model.enume.UserStatusEnum;
import io.github.liruohrh.apiplatform.model.req.user.UserLoginReq;
import io.github.liruohrh.apiplatform.model.req.user.UserNewPasswdReq;
import io.github.liruohrh.apiplatform.model.req.user.UserRegisterReq;
import io.github.liruohrh.apiplatform.model.req.user.UserUpdateReq;
import io.github.liruohrh.apiplatform.service.EmailService;
import io.github.liruohrh.apiplatform.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
* @author LYM
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-08-09 19:08:16
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

  private final EmailService emailService;
  public UserServiceImpl(
      EmailService emailService
  ) {
    this.emailService = emailService;
  }
  @Override
  public void register(UserRegisterReq req) {
    if(exists(new LambdaQueryWrapper<User>()
        .eq(User::getEmail, req.getEmail())
    )){
      throw new ParamException("邮箱已存在");
    }
    if(exists(new LambdaQueryWrapper<User>()
        .eq(User::getUsername, req.getUsername())
    )){
      throw new ParamException("用户名已存在");
    }

    emailService.verifyCaptcha(req.getEmail(), req.getCaptcha());
    User newUser = req.toUserEntity();

    //可以改为 SecureRandom，不需要固定SALT
    newUser.setAppKey(SecurityUtils.generateAppKey( newUser.getEmail()));
    newUser.setAppSecret(SecurityUtils.generateAppSecret(newUser.getEmail()));
    newUser.setPasswd(SecurityUtils.digestPasswd(newUser.getPasswd()));
    save(newUser);
  }

  @Override
  public User login(UserLoginReq userLoginReq) {
    User  loginUser = null;
    if(userLoginReq.getLoginType() == LoginType.CODE){
      User user = getOne(new LambdaQueryWrapper<User>()
          .eq(User::getEmail, userLoginReq.getEmail())
      );
      if(user == null){
        throw new ParamException(Resp.fail(ErrorCode.NO_REGISTER, "请先注册"));
      }
      if(!UserStatusEnum.COMMON.is(user.getStatus())){
        throw new BusinessException(ErrorCode.USER_FORBIDDEN);
      }
      emailService.verifyCaptcha(userLoginReq.getEmail(), userLoginReq.getCaptcha());
      loginUser = user;
    }if(userLoginReq.getLoginType() == LoginType.PASSWD){
      //密码登录：email、username同时存在，优先email
      User user = getOne(new LambdaQueryWrapper<User>()
          .eq(StrUtil.isNotEmpty(userLoginReq.getEmail()), User::getEmail, userLoginReq.getEmail())
          .eq(StrUtil.isEmpty(userLoginReq.getEmail()) && StrUtil.isNotEmpty(userLoginReq.getUsername()), User::getUsername,
              userLoginReq.getUsername())
          .eq(StrUtil.isNotEmpty(userLoginReq.getPasswd()), User::getPasswd,
              SecurityUtils. digestPasswd(userLoginReq.getPasswd())));
      if(user == null){
        throw new ParamException("请输入正确的密码登录参数");
      }
      if(!UserStatusEnum.COMMON.is(user.getStatus())){
        throw new BusinessException(ErrorCode.USER_FORBIDDEN);
      }
      loginUser = user;
    }
    LoginUtils.setLoginState(loginUser.getId());
    loginUser.setPasswd(null);
    return loginUser;
  }


  @Override
  public void newPasswd(UserNewPasswdReq userNewPasswdReq) {
    if(!exists(null, null, userNewPasswdReq.getEmail())){
      throw new ParamException("邮箱未注册");
    }
    emailService.verifyCaptcha(userNewPasswdReq.getEmail(), userNewPasswdReq.getCaptcha());
    MustUtils.dbSuccess(update(new LambdaUpdateWrapper<User>()
        .eq(User::getEmail, userNewPasswdReq.getEmail())
        .set(User::getPasswd, SecurityUtils.digestPasswd(userNewPasswdReq.getNewPasswd()))
    ));
  }

  @Override
  public void updateInfo(UserUpdateReq req) {
    User loginUser = LoginUserHolder.get();

    //admin 且 username=system才能更改role
    if(StrUtil.isNotEmpty(req.getRole())){
      if(!RoleEnum.ADMIN.is(loginUser.getRole()) ||  !RoleEnum.SYSTEM.is(loginUser.getUsername())){
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
      }
    }

    User oldUser = getById(req.getId());
    if(oldUser == null){
      throw new ParamException(Resp.fail(ErrorCode.NOT_EXISTS, "用户不存在"));
    }
    //system用户只有system自己可以修改
    if(RoleEnum.SYSTEM.is(oldUser.getUsername()) && !RoleEnum.SYSTEM.is(loginUser.getUsername())){
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    //无法更新system用户名
    if(StringUtils.isNotBlank(req.getUsername()) && RoleEnum.SYSTEM.is(oldUser.getUsername())){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    //非管理员不能更新其他人的
    if(!loginUser.getId().equals(req.getId()) && !RoleEnum.ADMIN.eq(loginUser.getRole())){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    if(StringUtils.isNotBlank(req.getEmail()) &&
        !StringUtils.equals(oldUser.getEmail(), req.getEmail()) &&
        exists(new LambdaQueryWrapper<User>()
        .eq(User::getEmail, req.getEmail())
    )){
      throw new ParamException("邮箱已存在");
    }
    if(StringUtils.isNotBlank(req.getUsername()) &&
        !StringUtils.equals(oldUser.getUsername(), req.getUsername()) &&
        exists(new LambdaQueryWrapper<User>()
            .eq(User::getUsername, req.getUsername())
        )){
      throw new ParamException("用户名已存在");
    }

    //管理员更新email不需要验证码
    if(StrUtil.isNotEmpty(req.getEmail())
        && !StringUtils.equals(oldUser.getEmail(), req.getEmail())
        && !RoleEnum.ADMIN.eq(loginUser.getRole())
    ){
      if(StrUtil.isEmpty(req.getCaptcha())){
        throw new ParamException("更新邮箱需要验证码");
      }
      emailService.verifyCaptcha(req.getEmail(), req.getCaptcha());
    }

    User user = BeanUtil.copyProperties(req, User.class);
    MustUtils.dbSuccess(updateById(user));
  }

  @Override
  public String resetAppSecret() {
    User loginUser = LoginUserHolder.get();
    loginUser.setAppSecret(SecurityUtils.generateAppSecret(loginUser.getEmail()));

    MustUtils.dbSuccess(update(new LambdaUpdateWrapper<User>()
        .eq(User::getId, loginUser.getId())
        .set(User::getAppSecret, loginUser.getAppSecret())
    ));
    return loginUser.getAppSecret();
  }

  private boolean exists(Long id, String name, String email) {
    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
        .select(User::getId);
    if (id != null) {
      return getOne(queryWrapper
          .eq(User::getId, id)) != null;
    } else if (name != null) {
      return getOne(queryWrapper
          .eq(User::getUsername, name)) != null;
    } else if (email != null) {
      return getOne(queryWrapper
          .eq(User::getEmail, email)) != null;
    }
    return false;
  }
}




