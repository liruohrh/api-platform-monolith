package io.github.liruohrh.apiplatform.api.controller;

import io.github.liruohrh.apiplatform.api.model.GLMMessage;
import io.github.liruohrh.apiplatform.api.model.GLMReq;
import io.github.liruohrh.apiplatform.api.model.GLMResp;
import io.github.liruohrh.apiplatform.apicommon.error.BusinessException;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequestMapping("/api/web")
@RestController
public class AIController {
  @Value("${zp-key}")
  private String zpKey;
  @Resource
  private RestTemplate restTemplate;

  @PostMapping("/cold-joke")
  public Resp<String> coldJoke(@RequestParam(value = "description", required = false) String description) {
    String url = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    GLMReq glmReq = new GLMReq();
    glmReq.setModel("glm-z1-flash");
//    glmReq.setModel("glm-zero-preview");
    ArrayList<GLMMessage> messages = new ArrayList<>();
    GLMMessage glmMessage = new GLMMessage();
    glmMessage.setRole("system");
    glmMessage.setContent("你是一个感情丰富、经历丰富、高情商、温柔的冷笑话专家");
    messages.add(glmMessage);
    GLMMessage glmMessage2 = new GLMMessage();
    glmMessage2.setRole("user");
    glmMessage2.setContent("请给我讲一个冷笑话，要求输出仅包含这个冷笑话，不允许包含其他的内容。");
    if(StringUtils.isNotBlank(description)){
      glmMessage2.setContent(glmMessage2.getContent() + "一些额外描述：" + description + "。");
    }
    messages.add(glmMessage2);
    glmReq.setMessages(messages);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set("Authorization", "Bearer "+zpKey);
    RequestEntity<GLMReq> req = new RequestEntity<>(
        glmReq,
        httpHeaders,
        HttpMethod.POST,
        URI.create(url)
    );
    ResponseEntity<String> resp = restTemplate.postForEntity(url, req, String.class);
    if (!resp.getStatusCode().is2xxSuccessful()) {
      log.warn("请求失败\n\t状态码: {}\n\theaders: {}", resp.getStatusCode(), resp.getHeaders());
      throw new BusinessException("系统异常，暂时无法使用");
    }
    String body = resp.getBody();
    if (StringUtils.isBlank(body)) {
      log.warn("请求失败, 空body。\n\t状态码: {}\n\theaders: {}", resp.getStatusCode(), resp.getHeaders());
      throw new BusinessException("系统异常，暂时无法使用");
    }
    log.info("resp: status={}, body={} ", resp.getStatusCode(), body);
    for (HttpMessageConverter<?> messageConverter : restTemplate.getMessageConverters()) {
      if (messageConverter.canRead(GLMResp.class, MediaType.APPLICATION_JSON)) {
        GLMResp bodyObj = null;
        try {
          bodyObj = (GLMResp) messageConverter.read((Class ) GLMResp.class, new HttpInputMessage() {
            @Override
            public InputStream getBody() throws IOException {
              return new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public HttpHeaders getHeaders() {
              return resp.getHeaders();
            }
          });
        } catch (IOException e) {
          log.warn("请求失败，无法解析body。\n\t状态码: {}\n\tbody={}\n\theaders: {}", resp.getStatusCode(), body, resp.getHeaders());
          throw new BusinessException("系统异常，暂时无法使用", e);
        }
        String data = bodyObj.generateMessage();
        int thinkI = data.indexOf("</think>");
        if(thinkI != -1){
          data = data.substring(thinkI + "</think>".length());
        }
        if(data.startsWith("\n")){
          data = data.substring(1);
        }
        return Resp.ok(data);
      }
    }
    log.warn("请求失败，无法解析body。\n\t状态码: {}\n\tbody={}\n\theaders: {}", resp.getStatusCode(), body, resp.getHeaders());
    throw new BusinessException("系统异常，暂时无法使用");
  }
}
