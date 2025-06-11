package io.github.liruohrh.apiplatform.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.digest.DigestUtil;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/oss")
@Controller
public class OssController {

  private final String port;

  public OssController(
      @Value("${server.port}") String port

  ) {
    this.port = port;
  }


  /**
   * @param file 存在则直接返回
   * @return
   * @throws IOException
   */
  @ResponseBody
  @PostMapping
  public String upload(@RequestPart("file") Part file, HttpServletRequest req) throws IOException {
    String url;
    String contentType = file.getContentType();
    if (contentType.startsWith("image/")) {
      if (!contentType.endsWith("jpeg") && !contentType.endsWith("png")) {
        throw new ParamException("不支持 jpeg/png外的图片");
      }
      url = addImg(
          file.getInputStream(),
          contentType.replace("image/", ""),
          "http://127.0.0.1:" + port + req.getContextPath()
      );
    } else {
      throw new ParamException("不支持 " + contentType);
    }
    return url;
  }

  public static final String IMAGES_DIR_PATH = resolveInRootDir("imgs");

  /**
   * @return 在 WebConfig#webMvcConfigurer 匹配拦截改url资源
   * @throws IOException
   */
  private static String addImg(InputStream inputStream, String type, String baseURL)
      throws IOException {
    byte[] bytes = IoUtil.readBytes(inputStream);
    String md5Hex = DigestUtil.md5Hex(bytes);
    String filename = md5Hex + "." + type;
    Path path = Paths.get(IMAGES_DIR_PATH, filename);
    if (path.toFile().exists()) {
      return baseURL + CommonConstant.OSS_LOCAL_URL_PATH_PREFIX + "imgs/" + filename;
    }
    Files.write(path, bytes);
    return baseURL + CommonConstant.OSS_LOCAL_URL_PATH_PREFIX + "imgs/" + filename;
  }

  public static String resolveInRootDir(String subPath) {
    Path rootDir = Paths.get(System.getProperty("user.home"), ".easyapi");
    File file = rootDir.toFile();
    if (!file.exists()) {
      file.mkdirs();
    }
    if (StringUtils.isBlank(subPath)) {
      return file.getAbsolutePath();
    }
    File target = Paths.get(file.getPath(), subPath).toFile();
    target.mkdirs();
    return target.toString();
  }
}
