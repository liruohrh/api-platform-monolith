package io.github.liruohrh.apiplatform.apicommon.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeatherReq {
  private String city;
  private String adcode;
  private String ip;
}
