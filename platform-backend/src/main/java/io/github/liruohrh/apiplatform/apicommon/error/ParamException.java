package io.github.liruohrh.apiplatform.apicommon.error;


public class ParamException extends BusinessException {
  public ParamException(String message) {
    super(Resp.fail(ErrorCode.PARAM, message));
  }
  public ParamException(Resp<Void> errorResp) {
    super(errorResp);
  }
}
