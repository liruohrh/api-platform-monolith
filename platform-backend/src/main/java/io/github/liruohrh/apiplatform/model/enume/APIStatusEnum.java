package io.github.liruohrh.apiplatform.model.enume;

public enum APIStatusEnum implements ValueEnum<Integer>{
  /**
   * 待审核 （用户可上传时可用）
   */
  WAIT_AUDITING(0),
  /**
   * 上线
   */
  LAUNCH (1),
  /**
   * 下线
   */
  ROLL_OFF(2),
  /**
   * 被禁用（用户可上传时可用）
   */
  FORBIDDEN(3),
  ;
  private final int value;

  APIStatusEnum(int value) {
    this.value = value;
  }

  @Override
  public Integer getValue() {
    return value;
  }
  public static boolean contains(Integer value){
    for (APIStatusEnum enumValue : values()) {
      if(enumValue.is(value)){
        return true;
      }
    }
    return false;
  }

  public static APIStatusEnum from(Integer value){
    for (APIStatusEnum enumValue : values()) {
      if(enumValue.is(value)){
        return enumValue;
      }
    }
    throw new IllegalArgumentException(APIStatusEnum.class.getSimpleName() + " has not value of " + value);
  }
}
