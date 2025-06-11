package io.github.liruohrh.apiplatform.api.weather.model;

import java.util.List;
import lombok.Data;

@Data
public class GaodeWeatherResp {
  //1：成功；0：失败
  private String status;
  //返回状态说明,10000代表正确
  private String infocode;
  public boolean success(){
    return "1".equals(status) && "10000".equals(infocode);
  }
  public Info first(){
    return forecasts.get(0);
  }
  private List<Info> forecasts;
  @Data
  public static class Info {
    //预报日期
    private String province;
    private String city;

    private List<Weather> casts;
  }
  @Data
  public static class Weather {
    private String date;
    private String dayweather;
    private String nightweather;
    private String daytemp;
    private String nighttemp;
  }
}