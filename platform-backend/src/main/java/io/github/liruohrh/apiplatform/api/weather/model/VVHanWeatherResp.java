package io.github.liruohrh.apiplatform.api.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VVHanWeatherResp {
  private String city;
  private Weather data;
  @Data
  public static class Weather {
    //预报日期
    private String date;
    private String type;
    @JsonProperty("night.type")
    private String nightType;
    private String low;
    private String high;
  }
}