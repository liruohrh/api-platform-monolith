package io.github.liruohrh.apiplatform.apicommon.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Resp<T> {

  private Integer code;
  private String msg;
  private T data;
  public   static Resp<Void> ok() {
    return new Resp<>(ErrorCode.SUCCESS.getCode(), null, null);
  }
  public   static <T> Resp<T> ok(T data) {
    return new Resp<>(ErrorCode.SUCCESS.getCode(), null, data);
  }
  public static Resp<Void> fail(ErrorCode ec) {
    return new Resp<>(ec.getCode(), ec.getMsg(), null);
  }
  /**
   * msg=ec.msg+', '+msg
   */
  public static Resp<Void> fail(ErrorCode ec, String msg) {
    return new Resp<>(ec.getCode(), ec.getMsg() + ", " + msg, null);
  }
}
