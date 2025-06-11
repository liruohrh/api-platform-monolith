package io.github.liruohrh.apiplatform.aop;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD })
@Retention(RUNTIME)
public @interface PreAuth {
  String mustRole() default "";
  String[] anyRoles() default {};
}
