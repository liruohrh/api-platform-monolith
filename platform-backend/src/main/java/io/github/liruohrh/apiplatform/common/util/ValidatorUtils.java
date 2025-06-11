package io.github.liruohrh.apiplatform.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.validation.ConstraintValidatorContext;

public class ValidatorUtils {

  public static void addErrorMessage(ConstraintValidatorContext context, String errorMessage) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(errorMessage)
        .addConstraintViolation();
  }

  /**
   * 比如更新用户时，有一个为非空，即id，其他都为空，就认定为All Null
   */
  public static boolean isAllNull(Object value, int leastNotNullCount){
    if(value == null) {
      return true;
    }
    int isNotNullCount = 0;
    for (Method method : value.getClass().getMethods()) {
      if(method.getName().equals("getClass")){
        continue;
      }
      if(
          (!Modifier.isStatic(method.getModifiers())  && Modifier.isPublic(method.getModifiers()))
              &&
              (    method.getName().startsWith("get") || method.getName().startsWith("is"))
              && method.getParameterCount() == 0 && (method.getReturnType() != void.class && method.getReturnType() != Void.class)
      ){
        try {
          if(method.invoke(value) != null){
            isNotNullCount++;
          }
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return isNotNullCount == leastNotNullCount;
  }
}
