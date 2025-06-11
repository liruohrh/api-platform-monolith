package io.github.liruohrh.apiplatform.rpc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.enume.UserStatusEnum;
import io.github.liruohrh.apiplatform.rpc.service.RpcUserService;
import io.github.liruohrh.apiplatform.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class RpcUserServiceImpl implements RpcUserService {
    private final UserService userService;

  public RpcUserServiceImpl(UserService userService) {
    this.userService = userService;
  }


  @Override
  public User getUserByAppKey(String appKey) {
    return userService.getOne(new LambdaQueryWrapper<User>()
            .eq(User::getStatus, UserStatusEnum.COMMON.getValue())
        .eq(User::getAppKey, appKey)
    );
  }
}
