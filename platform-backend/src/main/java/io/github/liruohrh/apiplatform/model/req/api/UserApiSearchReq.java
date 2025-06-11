package io.github.liruohrh.apiplatform.model.req.api;

import lombok.Data;

@Data
public class UserApiSearchReq {
  private String key;
  private Integer current;

  private Boolean isFree;
  private Boolean price;
  private Boolean leftTimes;
}
