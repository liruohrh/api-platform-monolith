package io.github.liruohrh.apiplatform.api;

import io.github.liruohrh.apiplatform.api.controller.AIController;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AITest {
  @Resource
  private AIController aiController;
  @Test
  public void test() {
    System.out.println(aiController.coldJoke("关于哪吒的"));
  }
}
