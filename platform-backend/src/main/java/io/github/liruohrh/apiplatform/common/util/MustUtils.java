package io.github.liruohrh.apiplatform.common.util;

import io.github.liruohrh.apiplatform.apicommon.error.ErrorCode;
import io.github.liruohrh.apiplatform.apicommon.error.BusinessException;

public class MustUtils {
  public static void mustTrue(boolean val, String message){
    if(!val){
      throw new RuntimeException("must true, but false, message: "+ message);
    }
  }
  public static void dbSuccess(boolean val){
    if(!val){
      throw new BusinessException(ErrorCode.OPERATION_DB);
    }
  }
}
