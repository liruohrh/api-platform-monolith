package io.github.liruohrh.apiplatform.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api-platform")
@Data
public class APIPlatformProperties {
  @NestedConfigurationProperty
  private Login login;

  @Data
  public static class Login{
    private List<String> whiteList;
  }
}
