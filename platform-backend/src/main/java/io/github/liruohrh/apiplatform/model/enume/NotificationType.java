package io.github.liruohrh.apiplatform.model.enume;

public enum NotificationType implements ValueEnum<Integer>{
  SYSTEM(0),
  COMMENT(1);
  private final Integer value;

  NotificationType(Integer value) {
    this.value = value;
  }

  @Override
  public Integer getValue() {
    return value;
  }

  public enum Comment implements ValueEnum<String>{
    REPLY("reply");
    private final String value;

    Comment(String value) {
      this.value = value;
    }

    @Override
    public String getValue() {
      return value;
    }
  }
}
