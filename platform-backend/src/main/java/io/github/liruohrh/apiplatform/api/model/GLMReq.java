package io.github.liruohrh.apiplatform.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class GLMReq {
  private String model;
  private List<GLMMessage> messages;
  @JsonProperty("max_tokens")
  private Integer maxTokens;
}
