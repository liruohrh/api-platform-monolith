package io.github.liruohrh.apiplatform.apicommon.error;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  SUCCESS(0, "success"),
  SYSTEM(500, "server system error"),
  GATEWAY(502, "gateway error"),

  SYSTEM_BUSY(50001, "system busy"),
  TIMEOUT(50010, "timeout"),
  OPERATION_DB(50020, "operate fail"),
  ALREADY_EXISTS(50030, "already exists"),
  NOT_EXISTS(50040, "not exists"),
  USER_FORBIDDEN(50050, "用户被冻结"),
  API_ROLL_OFF(50100, "API roll off"),
  API_CALL(50200, "API call error"),
  HAS_NOT_MORE_TIMES(50210, "has not more times of call api"),
  THIRD_API(51010, "third api call fail"),

  PARAM(4000, "client param error"),
  NO_REGISTER(40010, "has not register"),
  EXCEEDED_MAX_SIZE(40020, "exceeded max size"),
  ;
  private final Integer code;
  private final String msg;
}
