package io.github.liruohrh.apiplatform.model.vo;

import java.io.Serializable;
import lombok.Data;

@Data
public class ApiAndUsageVo implements Serializable {
  private Long id;
  private String logoUrl;
  private String name;
  private String description;
  private String method;
  private String protocol;
  private String domain;
  private String path;

  //评分、价格、创建时间或成交量
  private Double price;
  private Integer freeTimes;
  private Integer orderVolume;
  private Integer score;
  private Integer status;
  private Long ctime;
  private Long utime;


  private Integer leftTimes;
  private static final long serialVersionUID = 1L;
}
