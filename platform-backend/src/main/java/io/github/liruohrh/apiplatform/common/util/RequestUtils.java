package io.github.liruohrh.apiplatform.common.util;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

public class RequestUtils {
  public static Cookie getCookie(HttpServletRequest req, String name){
    Cookie[] cookies = req.getCookies();
    if(cookies == null){
      return null;
    }
    for (Cookie cookie : cookies) {
      if(cookie.getName().equals(name)){
        return cookie;
      }
    }
    return null;
  }
  public static Map<String, String> getHeaders(HttpServletRequest req){
    return Collections.list(req.getHeaderNames())
        .stream()
        .collect(Collectors.toMap(
            Function.identity(),
            req::getHeader
        ));
  }
  public static MultiValueMap<String, String> getHeaders2(HttpServletRequest req){
    HttpHeaders httpHeaders = new HttpHeaders();
    for (String headerName : Collections.list(req.getHeaderNames())) {
      httpHeaders.addAll(headerName, Collections.list(req.getHeaders(headerName)));
    }
    return httpHeaders;
  }
}
