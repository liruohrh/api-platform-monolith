package io.github.liruohrh.apiplatform.api.controller;

import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.controller.OssController;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/web/random")
public class RandomController {
  private String baseUrl;
  private List<String> emojiUrlList;
  private List<String> wallpaperUrlList;
  public RandomController(
      @Value("${server.port}")
      String port,
      @Value("${server.servlet.context-path}")
      String contextPath
  ) {
    this.baseUrl = "http://127.0.0.1:"+port + contextPath + CommonConstant.OSS_LOCAL_URL_PATH_PREFIX;
    this.emojiUrlList = Arrays.stream(new File( OssController.resolveInRootDir("api/emoji")).list())
        .map(e -> this.baseUrl + "api/emoji/" + e)
        .collect(Collectors.toList());
    this.wallpaperUrlList = Arrays.stream(new File( OssController.resolveInRootDir("api/wallpaper")).list())
        .map(e -> this.baseUrl + "api/wallpaper/" + e)
        .collect(Collectors.toList());
  }

  @GetMapping("/emoji")
  public Resp<String> emoji() {
    return Resp.ok(emojiUrlList.get(new Random().nextInt(emojiUrlList.size())));
  }
  @GetMapping("/wallpaper")
  public Resp<String> wallpaper() {
    return Resp.ok(wallpaperUrlList.get(new Random().nextInt(wallpaperUrlList.size())));
  }
  @GetMapping("/pet-name")
  public Resp<String> petName() {
    return Resp.ok(wallpaperUrlList.get(new Random().nextInt(wallpaperUrlList.size())));
  }
  @GetMapping("/human-name")
  public Resp<String> humanName() {
    return Resp.ok(wallpaperUrlList.get(new Random().nextInt(wallpaperUrlList.size())));
  }
}
