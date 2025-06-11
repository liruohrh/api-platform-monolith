package io.github.liruohrh.apiplatform.model.resp.api;

import java.io.Serializable;
import lombok.Data;
import org.springframework.util.MultiValueMap;

@Data
public class ApiCallResp implements Serializable {
  private Integer status;
  private String body;
  private MultiValueMap<String, String> headers;
  private static final long serialVersionUID = 1L;
}
