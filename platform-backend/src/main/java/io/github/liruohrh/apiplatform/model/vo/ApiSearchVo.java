package io.github.liruohrh.apiplatform.model.vo;

import java.io.Serializable;
import lombok.Data;

@Data
public class ApiSearchVo implements Serializable {
  private Long id;
  private String logoUrl;
  private String name;
  private String description;

  //评分、价格、创建时间或成交量
  private Double price;
  private Integer orderVolume;
  private Integer score;
  private Long ctime;
  private static final long serialVersionUID = 1L;
}
