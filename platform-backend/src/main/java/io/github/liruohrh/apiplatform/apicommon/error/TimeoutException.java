package io.github.liruohrh.apiplatform.apicommon.error;


public class TimeoutException extends BusinessException {
  public TimeoutException(String message) {
    super(Resp.fail(ErrorCode.TIMEOUT, message));
  }
  public TimeoutException(Resp<Void> errorResp) {
    super(errorResp);
  }
}
