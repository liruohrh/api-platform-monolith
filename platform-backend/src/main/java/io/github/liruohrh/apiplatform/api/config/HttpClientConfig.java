package io.github.liruohrh.apiplatform.api.config;

import io.github.liruohrh.apiplatform.webcommon.spring.SpringRestTemplateErrorHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfig {
  @Bean
  public RestTemplate restTemplate(){
    return new RestTemplateBuilder()
        .errorHandler(new SpringRestTemplateErrorHandler())
        .build();
  }


}
