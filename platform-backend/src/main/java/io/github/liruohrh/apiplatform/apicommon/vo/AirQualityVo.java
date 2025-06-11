package io.github.liruohrh.apiplatform.apicommon.vo;

import lombok.Data;

@Data
public class AirQualityVo {

  // 空气质量指数
  private int aqi;
  // 空气质量等级
  private int aqiLevel;
  // 空气质量等级名称
  private String aqiName;
  // 一氧化碳浓度
  private String co;
  // 二氧化氮浓度
  private String no2;
  // 臭氧浓度
  private String o3;
  // 可吸入颗粒物(PM10)浓度
  private String pm10;
  // 细颗粒物(PM2.5)浓度
  private String pm25;
  // 二氧化硫浓度
  private String so2;
}