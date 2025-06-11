package io.github.liruohrh.apiplatform.apicommon.resp;

import lombok.Data;

@Data
public class WeatherResp {

  /**
   * 地址，如广东/惠州，惠州市，中国/广东省/惠州
   */
  private String location;
  /**
   * 日期。如 2002-01-01
   */
  private String date;
  /**
   * 天气描述。如多云，多云/雾
   */
  private String description;

  /**
   * 最低温度，如10°C
   */
  private String lowerT;

  /**
   * 最高温度，如10°C
   */
  private String highT;
}
