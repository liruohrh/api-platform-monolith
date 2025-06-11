package io.github.liruohrh.apiplatform.model.req;

import lombok.Data;

@Data
public class ApiUsageReq {
  private Integer year;
  private Integer month;
  private String username;
  private String apiName;
}
