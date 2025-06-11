package io.github.liruohrh.apiplatform.api.weather.model;

import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class HeFengWeatherResp {
  private String code;
  public boolean success(){
    return StringUtils.isBlank(code) || code.equals("200");
  }

  private List<Weather> daily;

  @Data
  public static class Weather {
    //预报日期
    private String fxDate;
    private String textDay;
    private String textNight;
    private String tempMin;
    private String tempMax;
  }
}