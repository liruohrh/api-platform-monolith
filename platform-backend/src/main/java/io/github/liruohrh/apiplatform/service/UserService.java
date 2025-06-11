package io.github.liruohrh.apiplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.req.user.UserLoginReq;
import io.github.liruohrh.apiplatform.model.req.user.UserNewPasswdReq;
import io.github.liruohrh.apiplatform.model.req.user.UserRegisterReq;
import io.github.liruohrh.apiplatform.model.req.user.UserUpdateReq;

/**
* @author LYM
* @description 针对表【user】的数据库操作Service
* @createDate 2024-08-09 19:08:16
*/
public interface UserService extends IService<User> {

  /**
   * 不同时登录，方便别人注册，又保持自己的登录态
   */

  void register(UserRegisterReq userRegisterReq);

  User login(UserLoginReq userLoginReq);


  /**
   * 暂时不要求重新登录，因为没有管理登录会话
   */
  void newPasswd(UserNewPasswdReq userNewPasswdReq);

  /**
   * @param userUpdateReq
   */
  void updateInfo(UserUpdateReq userUpdateReq);

  String  resetAppSecret();

}
