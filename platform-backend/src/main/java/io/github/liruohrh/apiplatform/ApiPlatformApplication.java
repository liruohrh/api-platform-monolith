package io.github.liruohrh.apiplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@MapperScan("io.github.liruohrh.apiplatform.mapper")
@SpringBootApplication
public class ApiPlatformApplication {

  public static void main(String[] args) {
    new SpringApplication(ApiPlatformApplication.class).run(args);
  }

}
