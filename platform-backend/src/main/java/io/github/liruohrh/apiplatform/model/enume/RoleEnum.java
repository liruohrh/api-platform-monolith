package io.github.liruohrh.apiplatform.model.enume;

public enum RoleEnum implements ValueEnum<String>{
  USER("USER"),
  ADMIN("ADMIN"),
  /**
   * admin 且 username=system才能更改role
   */
  SYSTEM("system"),
  ;
  private final String value;

  RoleEnum(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }
  public boolean eq(String role) {
    return value.equals(role);
  }
}
