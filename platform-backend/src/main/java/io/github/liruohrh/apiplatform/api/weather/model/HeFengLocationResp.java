package io.github.liruohrh.apiplatform.api.weather.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class HeFengLocationResp {
  private String code;
  public boolean success(){
    return StringUtils.isBlank(code) || code.equals("200");
  }
  public Location first(){
    return location.get(0);
  }
  private List<Location> location;

  @Data
  public static class Location {
    //和风的locationId
    private String id;
    //    location.country 地区/城市所属国家名称
    private String country;
    //    location.adm1 地区/城市所属一级行政区域
    private String adm1;
    //    location.adm2 地区/城市的上级行政区划名称
    private String adm2;
    public String location(){
      return Stream.of(country, adm1, adm2)
          .filter(StringUtils::isNotBlank)
          .collect(Collectors.joining("/"));
    }
  }
}