package io.github.liruohrh.apiplatform.apicommon.vo;

import java.time.LocalDate;
import lombok.Data;

@Data
public class WeatherInfoVo {
  //第几天
  private LocalDate date;
  //周几
  private String week;
  //天气类型，如：阴，大雨
  private String type;

  private String lowDegreesCelsius;
  private String highDegreesCelsius;
  private String windDirection;
  //风力
  private String windForce;
  private WeatherInfoVo night;
}
