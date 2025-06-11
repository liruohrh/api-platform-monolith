package io.github.liruohrh.apiplatform.api.weather;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.liruohrh.apiplatform.api.weather.model.HeFengLocationResp;
import io.github.liruohrh.apiplatform.api.weather.model.HeFengWeatherResp;
import io.github.liruohrh.apiplatform.api.weather.model.HeFengWeatherResp.Weather;
import io.github.liruohrh.apiplatform.apicommon.error.ThirdAPIException;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.apicommon.req.WeatherReq;
import io.github.liruohrh.apiplatform.apicommon.resp.WeatherResp;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(500)
@Slf4j
@Component
public class HeFengWeatherHandler implements WeatherHandler {

  @Value("${hefeng.api-key}")
  private String apiKey;

  @Override
  public boolean canHandler(WeatherReq req) {
    return StrUtil.isNotBlank(req.getCity());
  }

  /*
    使用情况：https://console.qweather.com/#/statistics
    费用：https://dev.qweather.com/docs/finance/pricing/
      免费：0~5万/月

     先查城市对应的locationId：https://dev.qweather.com/docs/api/geoapi/city-lookup/
         GET https://geoapi.qweather.com/v2/city/lookup
           args:
            key=
            location= 只要是个区域就行

     再根据locationId获取天气：https://dev.qweather.com/docs/api/weather/weather-daily-forecast/
         GET https://devapi.qweather.com/v7/weather/3d
           args:
            key=
            location= 区域id
     */
  @Override
  public WeatherResp getWeather(WeatherReq req) {
    if(StringUtils.isAllEmpty(req.getCity())){
      throw new ParamException("city is required");
    }

    HeFengLocationResp locationResp = getLocationId(req.getCity());
    String url = UrlBuilder.of("https://devapi.qweather.com/v7/weather/3d")
        .addQuery("key", apiKey)
        .addQuery("location", locationResp.first().getId())
        .build();
    try (HttpResponse httpResponse = HttpUtil.createGet(url).timeout(25000).execute()) {
      log.info("url={}, status={}, body={}", url, httpResponse.getStatus(), httpResponse.body());
      if (!httpResponse.isOk()) {
        throw new ThirdAPIException("获取天气信息失败");
      }
      HeFengWeatherResp respBody = null;
      try {
        respBody = objectMapper.readValue(httpResponse.body(), HeFengWeatherResp.class);
      } catch (JsonProcessingException e) {
        throw new ThirdAPIException("解析天气信息失败", e);
      }
      if (!respBody.success()) {
        throw new ThirdAPIException("解析天气信息失败");
      }
      if (CollectionUtil.isEmpty(respBody.getDaily())) {
        throw new ThirdAPIException("解析天气信息失败");
      }
      Weather currentDay = respBody.getDaily().get(0);
      if(StringUtils.isEmpty(currentDay.getFxDate())){
        throw new ThirdAPIException("解析城市信息失败，无日期");
      }
      if(StringUtils.isAllEmpty(
          currentDay.getTextDay(),
          currentDay.getTextNight()
      )){
        throw new ThirdAPIException("解析天气信息失败，无天气描述");
      }
      if(StringUtils.isAllEmpty(
          currentDay.getTempMax(),
          currentDay.getTempMin()
      )){
        throw new ThirdAPIException("解析天气信息失败，无温度");
      }
      WeatherResp result = new WeatherResp();
      result.setLocation(locationResp.first().location());
      result.setDate(currentDay.getFxDate());
      result.setDescription(
          Stream.of(currentDay.getTextDay(), currentDay.getTextNight())
              .filter(Objects::nonNull)
              .collect(Collectors.joining("/"))
      );
      result.setLowerT(currentDay.getTempMin()+"°C");
      result.setHighT(currentDay.getTempMax()+"°C");
      return result;
    }
  }

  public HeFengLocationResp getLocationId(String city) {
    String url = UrlBuilder.of("https://geoapi.qweather.com/v2/city/lookup")
        .addQuery("key", apiKey)
        .addQuery("location", city)
        .build();
    try (HttpResponse locationResp = HttpUtil.createGet(url).timeout(25000).execute()) {
      log.info("url={}, status={}, body={}", url, locationResp.getStatus(), locationResp.body());
      if (!locationResp.isOk()) {
        throw new ThirdAPIException("获取城市信息失败");
      }
      HeFengLocationResp respBody = null;
      try {
        respBody = objectMapper.readValue(locationResp.body(),
            HeFengLocationResp.class);
      } catch (JsonProcessingException e) {
        throw new ThirdAPIException("获取城市信息失败", e);
      }

      if (!respBody.success()) {
        throw new ThirdAPIException("解析城市信息失败");
      }


      if (CollectionUtil.isEmpty(respBody.getLocation())) {
        throw new ThirdAPIException("解析城市信息失败");
      }
      String locationId = respBody.getLocation().get(0).getId();
      if (StringUtils.isBlank(locationId)) {
        throw new ThirdAPIException("解析城市信息失败");
      }
      return respBody;
    }
  }

}
