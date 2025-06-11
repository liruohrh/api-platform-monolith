package io.github.liruohrh.apiplatform.aop;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.model.entity.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class AuthAdvice {

  @Around("execution(* io.github.liruohrh.apiplatform.controller.*.*(..)) && @annotation(preAuth)")
  public Object validateAuthority(ProceedingJoinPoint joinPoint, PreAuth preAuth) throws Throwable {
    User loginUser = LoginUserHolder.get();
    if(!StrUtil.isEmpty(preAuth.mustRole()) && !preAuth.mustRole().equals(loginUser.getRole())){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    if(!ArrayUtil.isEmpty(preAuth.anyRoles()) && !ArrayUtil.contains(preAuth.anyRoles(), loginUser.getRole())){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    return joinPoint.proceed();
  }
}
