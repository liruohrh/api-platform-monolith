package io.github.liruohrh.apiplatform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("api-gateway")
public class ApiGatewayProperties {
  @NestedConfigurationProperty
  private ReplayAttack replayAttack;
  @Data
  public static class ReplayAttack{
    private Integer maxAliveTime;
  }
}
