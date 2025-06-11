package io.github.liruohrh.apiplatform.model.enume;

public enum ApplicationAuditStatus implements ValueEnum<Integer> {
  PENDING_AUDIT(0),
  APPROVED(1),
  NOT_PASS(2),
  CANCEL(20),
  ;
  private final Integer value;

  ApplicationAuditStatus(Integer value) {
    this.value = value;
  }

  @Override
  public Integer getValue() {
    return value;
  }
}
