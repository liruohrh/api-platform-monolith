package io.github.liruohrh.apiplatform.model.resp;

import lombok.Data;

@Data
public class OrderStatusResp {
  private String date;
  private Long apiId;
  private String apiName;
  private Double amount;
  private Integer total;
}
