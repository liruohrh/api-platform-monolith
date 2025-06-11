package io.github.liruohrh.apiplatform.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
@Data
public class GLMResp {
  private List<Choice> choices; // 当前对话的模型输出内容
  @Data
  public static class Choice {
    private Integer index; // 结果索引
    @JsonProperty("finish_reason")
    private String finishReason; // 模型推理终止的原因
    private GLMMessage message; // 模型返回的文本消息
  }
  public String generateMessage() {
    return choices.stream().map(e->e.getMessage().getContent()).collect(Collectors.joining("\n"));
  }
}
