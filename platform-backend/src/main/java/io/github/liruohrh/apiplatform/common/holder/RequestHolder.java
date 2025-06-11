package io.github.liruohrh.apiplatform.common.holder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestHolder {
  private static final ThreadLocal<HttpServletRequest> reqThreadLocal = new ThreadLocal<>();
  private static final ThreadLocal<HttpServletResponse> respThreadLocal = new ThreadLocal<>();
  public static void set(HttpServletRequest subject){
    reqThreadLocal.set(subject);
  }
  public static HttpServletRequest get(){
    return reqThreadLocal.get();
  }
  public static void setResp(HttpServletResponse subject){
    respThreadLocal.set(subject);
  }
  public static HttpServletResponse getResp(){
    return respThreadLocal.get();
  }

  public static void clear() {
    reqThreadLocal.remove();
    respThreadLocal.remove();
  }
}
