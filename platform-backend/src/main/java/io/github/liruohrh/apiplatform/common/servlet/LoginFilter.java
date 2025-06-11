package io.github.liruohrh.apiplatform.common.servlet;

import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.common.util.LoginUtils;
import io.github.liruohrh.apiplatform.service.UserService;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public class LoginFilter extends OncePerRequestFilter {
  @Data
  static class ResourceMatcher{
    private HttpMethod method;
    private PathPattern pattern;
  }
  private final List<ResourceMatcher> whiteList;
  private final UserService userService;

  public LoginFilter(
      List<String> whiteList,
      UserService userService

  ) {
    PathPatternParser pathPatternParser = new PathPatternParser();
    this.whiteList = whiteList.stream().map(white->{
          String[] split = white.split(",");
          ResourceMatcher resourceMatcher = new ResourceMatcher();
          if(split.length == 1){
            resourceMatcher.setMethod(null);
            resourceMatcher.setPattern(pathPatternParser.parse(white));
          }else{
            resourceMatcher.setMethod(HttpMethod.resolve(split[0]));
            resourceMatcher.setPattern(pathPatternParser.parse(split[1]));
          }
          return resourceMatcher;
        })
        .collect(Collectors.toList());
    this.userService = userService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
   try {
     String method = request.getMethod();
     if(
         !(HttpMethod.GET.matches(method)
             || HttpMethod.POST.matches(method)
             || HttpMethod.PUT.matches(method)
             || HttpMethod.DELETE.matches(method))
     ){
       filterChain.doFilter(request, response);
       return;
     }

     String requestURI = request.getRequestURI();
     requestURI = requestURI.replace(request.getContextPath(), "");
     PathContainer pathContainer = PathContainer.parsePath(requestURI);
     HttpSession session = request.getSession(false);
     if(whiteList.stream().anyMatch(whitePath->
         (whitePath.getMethod() == null || whitePath.getMethod().matches(method))
             && whitePath.getPattern().matches(pathContainer)
     )){
       if(session != null){
         Long loginUserId = LoginUtils.getLoginState(session);
         if(loginUserId != null){
           LoginUserHolder.set(loginUserId, ()->userService.getById(loginUserId));
         }
       }
       filterChain.doFilter(request, response);
       return;
     }

     //必须登录
     if(session == null){
       hasNotLogin(request, response);
       return;
     }
     Long loginUserId = LoginUtils.getLoginState(request.getSession());
     if(loginUserId == null){
       hasNotLogin(request, response);
       return;
     }
     LoginUserHolder.set(loginUserId, ()->userService.getById(loginUserId));
     filterChain.doFilter(request, response);
   }finally {
     LoginUserHolder.clear();
   }
  }

  private void hasNotLogin(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.sendError(HttpStatus.UNAUTHORIZED.value());
  }
}
