package io.github.liruohrh.apiplatform.model.req.api;

import lombok.Data;

@Data
public class ApiSearchReq {
  private String key;
  private Integer current;

  private Boolean isFree;
  private Boolean price;
  private Boolean orderVolume;
  private Boolean score;
  private Boolean ctime;
}
