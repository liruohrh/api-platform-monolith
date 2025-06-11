package io.github.liruohrh.apiplatform.common.util;

import io.github.liruohrh.apiplatform.apicommon.utils.ServletUtils;
import io.github.liruohrh.apiplatform.constant.RedisConstant;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class LoginUtils {
  public static Long getLoginState(
      HttpSession session
  ) {
    return (Long) session.getAttribute(RedisConstant.PREFIX_LOGIN);
  }


  public static boolean clearLoginState(
  ) {
    HttpServletRequest req = ServletUtils.getRequest();
    HttpSession session = req.getSession(true);
    if (session == null) {
      return false;
    }
    session.invalidate();
    return  true;
  }

  public static void setLoginState(
      Long userId
  ) {
    HttpServletRequest req = ServletUtils.getRequest();
    HttpSession session = req.getSession();
    session.setAttribute(RedisConstant.PREFIX_LOGIN, userId);
  }
}
