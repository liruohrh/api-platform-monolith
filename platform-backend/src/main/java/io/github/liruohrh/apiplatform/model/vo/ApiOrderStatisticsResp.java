package io.github.liruohrh.apiplatform.model.vo;

import lombok.Data;

@Data
public class ApiOrderStatisticsResp {
  private String date;
  private Integer amount;
  private Double total;
  public static ApiOrderStatisticsResp of(String date){
    ApiOrderStatisticsResp e = new ApiOrderStatisticsResp();
    e.setDate(date);
    e.setAmount(0);
    e.setTotal(0.0);
    return e;
  }
}
