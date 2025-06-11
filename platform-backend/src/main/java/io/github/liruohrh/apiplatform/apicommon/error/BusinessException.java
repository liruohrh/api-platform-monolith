package io.github.liruohrh.apiplatform.apicommon.error;



public class BusinessException extends RuntimeException {
  public final Resp<Void> errorResp;
  public BusinessException(String message) {
    super(message);
    this.errorResp = Resp.fail(ErrorCode.SYSTEM, message);
  }
  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMsg());
    this.errorResp = Resp.fail(errorCode);
  }
  public BusinessException(ErrorCode errorCode, String msg) {
    super(msg);
    this.errorResp = Resp.fail(errorCode, msg);
  }
  public BusinessException(Resp<Void> errorResp) {
    super(errorResp.getMsg());
    this.errorResp = errorResp;
  }
  public BusinessException(Resp<Void> errorResp, Throwable cause) {
    super(errorResp.getMsg(), cause);
    this.errorResp = errorResp;
  }
  public BusinessException(String message, Throwable cause) {
    super(message, cause);
    this.errorResp = Resp.fail(ErrorCode.SYSTEM);
  }
}
