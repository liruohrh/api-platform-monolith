package io.github.liruohrh.apiplatform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.liruohrh.apiplatform.aop.PreAuth;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.util.ValidatorUtils;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.req.user.UserSearchReq;
import io.github.liruohrh.apiplatform.model.req.user.UserUpdateReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.vo.UserVo;
import io.github.liruohrh.apiplatform.service.UserService;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
@Validated
public class UserController {

  private final UserService userService;

  public UserController(
      UserService userService
  ) {
    this.userService = userService;
  }
  @PreAuth(mustRole = "ADMIN")
  @GetMapping("/{userId}")
  public Resp<UserVo> getUserById(@PathVariable("userId") Long userId) {
    User user = userService.getById(userId);
    user.setPasswd(null);
    user.setAppSecret(null);
    return Resp.ok(BeanUtil.copyProperties(user, UserVo.class));
  }

  @PreAuth(mustRole = "ADMIN")
  @GetMapping("/list")
  public Resp<PageResp<UserVo>> listUser(UserSearchReq req) {
    if(req.getId() != null){
      return Resp.ok(PageResp.one(BeanUtil.copyProperties(
          userService.getById(req.getId()),
          UserVo.class)));
    }else if(StringUtils.isNotBlank(req.getEmail())){
      return Resp.ok(PageResp.one(BeanUtil.copyProperties(
          userService.getOne(new LambdaQueryWrapper<User>()
              .eq(User::getEmail, req.getEmail())),
          UserVo.class)));
    }else if(StringUtils.isNotBlank(req.getAppKey())){
      return Resp.ok(PageResp.one(BeanUtil.copyProperties(
          userService.getOne(new LambdaQueryWrapper<User>()
              .eq(User::getAppKey, req.getAppKey())),
          UserVo.class)));
    }
    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

    if(StringUtils.isNotBlank(req.getNickname())){
      queryWrapper.like(User::getNickname, req.getNickname());
    }
    if(StringUtils.isNotBlank(req.getUsername())){
      queryWrapper.like(User::getUsername, req.getUsername());
    }
    if(StringUtils.isNotBlank(req.getPersonalDescription())){
      queryWrapper.like(User::getPersonalDescription, req.getPersonalDescription());
    }
    if(StringUtils.isNotBlank(req.getRole())){
      queryWrapper.eq(User::getRole, req.getRole());
    }
    if(req.getStatus() != null){
      queryWrapper.eq(User::getStatus, req.getStatus());
    }
    if(req.getCtime() != null && req.getCtime().size() == 2){
      if(req.getCtime().get(0).equals(req.getCtime().get(1))){
        queryWrapper.eq(User::getCtime, req.getCtime().get(0));
      }else{
        queryWrapper.between(User::getCtime, req.getCtime().get(0), req.getCtime().get(1));
      }
    }

    queryWrapper.orderBy(req.getCtimeS() != null, Boolean.TRUE.equals(req.getCtimeS()), User::getCtime);

    Page<User> page =  userService.page(
        new Page<>(req.getCurrent(), CommonConstant.PAGE_MAX_SIZE_USER),
        queryWrapper
    );

    return Resp.ok(new PageResp<>(
        page.getRecords().stream()
            .map(user -> BeanUtil.copyProperties(user, UserVo.class))
            .collect(Collectors.toList()),
        page.getTotal(),
        page.getCurrent(),
        page.getPages(),
        page.getSize()
    ));
  }

  @PutMapping
  public Resp<Boolean> updateUser(@RequestBody @Validated UserUpdateReq userUpdateReq) {
    if (!ValidatorUtils.isAllNull(userUpdateReq, 1)) {
      userService.updateInfo(userUpdateReq);
      return Resp.ok(true);
    }
    return Resp.ok(false);
  }
}
