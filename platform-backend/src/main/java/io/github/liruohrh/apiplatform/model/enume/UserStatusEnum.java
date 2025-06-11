package io.github.liruohrh.apiplatform.model.enume;


public enum UserStatusEnum implements ValueEnum<Integer>{
  COMMON(0),
  FREEZE_USER(1),
  FREEZE_PROVIDER(2),
  FREEZE_BOTH(3),
  ;
  private final int value;

  UserStatusEnum(int value) {
    this.value = value;
  }

  @Override
  public Integer getValue() {
    return value;
  }
}
