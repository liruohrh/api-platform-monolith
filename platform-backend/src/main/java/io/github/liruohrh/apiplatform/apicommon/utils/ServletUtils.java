package io.github.liruohrh.apiplatform.apicommon.utils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ServletUtils {


  /**
   * 获取request
   */
  public static HttpServletRequest getRequest()
  {
    return getRequestAttributes().getRequest();
  }

  /**
   * 获取response
   */
  public static HttpServletResponse getResponse()
  {
    return getRequestAttributes().getResponse();
  }

  /**
   * 获取session
   */
  public static HttpSession getSession()
  {
    return getRequest().getSession();
  }

  public static ServletRequestAttributes getRequestAttributes()
  {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    return (ServletRequestAttributes) attributes;
  }

}
