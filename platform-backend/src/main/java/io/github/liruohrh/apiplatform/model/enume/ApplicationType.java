package io.github.liruohrh.apiplatform.model.enume;

public enum ApplicationType implements ValueEnum<Integer>{
  COMMENT_REPORT(1),
  ORDER_FOUND(2);
  private final Integer value;

  ApplicationType(Integer value) {
    this.value = value;
  }

  @Override
  public Integer getValue() {
    return value;
  }
}
