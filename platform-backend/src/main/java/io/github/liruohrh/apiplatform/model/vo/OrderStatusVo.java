package io.github.liruohrh.apiplatform.model.vo;

import lombok.Data;

@Data
public class OrderStatusVo {
  private String date;
  private Long apiId;
  private String apiName;
  private Double amount;
  private Integer total;
}
