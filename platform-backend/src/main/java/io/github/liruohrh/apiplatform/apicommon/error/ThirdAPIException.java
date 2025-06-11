package io.github.liruohrh.apiplatform.apicommon.error;


public class ThirdAPIException extends BusinessException {
  public ThirdAPIException(String message) {
    super(Resp.fail(ErrorCode.THIRD_API, message));
  }
  public ThirdAPIException(String message, Throwable cause) {
    super(Resp.fail(ErrorCode.THIRD_API, message), cause);
  }
  public ThirdAPIException(Resp<Void> errorResp) {
    super(errorResp);
  }
}
