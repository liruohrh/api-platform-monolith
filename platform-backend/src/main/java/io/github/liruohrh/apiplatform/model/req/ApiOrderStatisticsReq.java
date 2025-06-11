package io.github.liruohrh.apiplatform.model.req;

import lombok.Data;

@Data
public class ApiOrderStatisticsReq {
  private Integer year;
  private Integer month;
  private Long apiId;
  private Long userId;
}
