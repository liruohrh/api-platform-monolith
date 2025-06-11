package io.github.liruohrh.apiplatform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.liruohrh.apiplatform.aop.PreAuth;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.util.SecurityUtils;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.enume.RoleEnum;
import io.github.liruohrh.apiplatform.model.req.user.UserAddReq;
import io.github.liruohrh.apiplatform.service.UserService;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuth(mustRole = "ADMIN")
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

  @Resource
  private UserService userService;

  @PostMapping
  public Resp<Void> addUser(@RequestBody  UserAddReq req) {
    if(!RoleEnum.USER.is(req.getRole()) && !RoleEnum.ADMIN.is(req.getRole())){
      throw new ParamException("非法角色");
    }
    if(userService.exists(new LambdaQueryWrapper<User>()
        .eq(User::getEmail, req.getEmail())
    )){
      throw new ParamException("邮箱已存在");
    }
    if(userService.exists(new LambdaQueryWrapper<User>()
        .eq(User::getUsername, req.getUsername())
    )){
      throw new ParamException("用户名已存在");
    }
    if(StringUtils.isEmpty(req.getNickname())){
      req.setNickname(req.getUsername());
    }

    User newUser = req.toUserEntity();
    //可以改为 SecureRandom，不需要固定SALT
    newUser.setAppKey(SecurityUtils.generateAppKey(newUser.getEmail()));
    newUser.setAppSecret(SecurityUtils.generateAppSecret(newUser.getEmail()));
    newUser.setPasswd(SecurityUtils.digestPasswd(newUser.getPasswd()));
    userService.save(newUser);
    return Resp.ok(null);
  }
}
