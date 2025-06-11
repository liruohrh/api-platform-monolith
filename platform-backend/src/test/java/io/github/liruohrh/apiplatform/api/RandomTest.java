package io.github.liruohrh.apiplatform.api;

import io.github.liruohrh.apiplatform.api.controller.RandomController;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RandomTest {
  @Resource
  private RandomController randomController;


  @Test
  public void test() {
    System.out.println(randomController.emoji());
    System.out.println(randomController.wallpaper());
  }
}
