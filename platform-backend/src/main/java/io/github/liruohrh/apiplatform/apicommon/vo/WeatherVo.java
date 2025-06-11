package io.github.liruohrh.apiplatform.apicommon.vo;

import java.util.List;
import lombok.Data;

@Data
public class WeatherVo {
  String city;
  List<WeatherInfoVo> weatherList;
  AirQualityVo air;
}
