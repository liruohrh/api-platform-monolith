package io.github.liruohrh.apiplatform.api.weather;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.liruohrh.apiplatform.api.locatioin.GaoDeAdcodeResp;
import io.github.liruohrh.apiplatform.api.locatioin.GaoDeAdcodeResp.Location;
import io.github.liruohrh.apiplatform.api.locatioin.LocationHandler;
import io.github.liruohrh.apiplatform.api.utils.ParseUtils;
import io.github.liruohrh.apiplatform.api.weather.model.GaodeWeatherResp;
import io.github.liruohrh.apiplatform.api.weather.model.GaodeWeatherResp.Weather;
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

@Order(100)
@Slf4j
@Component
public class GaoDeWeatherHandler implements WeatherHandler, LocationHandler {
  @Value("${gaode.api-key}")
  private String apiKey;


  @Override
  public boolean canHandler(WeatherReq req) {
    return !StringUtils.isAllBlank(req.getCity(), req.getAdcode());
  }

  /*
     https://lbs.amap.com/api/webservice/guide/api/weatherinfo
     使用情况：https://console.amap.com/dev/flow/manage/ca5cd3fb40f736b9491d48bbbdc1d208
         目前找不到这个天气查询的使用情况，暂时不管吧，应该也有个5000次/日的
     GET https://api.vvhan.com/api/weather
       args:
        无：当前ip
        ip: optional
        city: optional
        type: optional, 默认一天，week则一周
     */
  @Override
  public WeatherResp getWeather(WeatherReq req) {
    if(StringUtils.isAllEmpty(req.getCity(), req.getAdcode())){
      throw new ParamException("city or adcode is required");
    }

    UrlBuilder urlBuilder = UrlBuilder.of("https://restapi.amap.com/v3/weather/weatherInfo");
    urlBuilder.addQuery("key", apiKey);
    String adcode = req.getAdcode();
    if(StringUtils.isBlank(adcode)){
      GaoDeAdcodeResp adcodeResp = getAdCode(req.getCity());
      adcode = adcodeResp.first().getAdcode();
    }
    urlBuilder.addQuery("city", adcode);
    urlBuilder.addQuery("extensions", "all");
    String url = urlBuilder.build();
    try (HttpResponse httpResponse = HttpUtil.createGet(url)
        .timeout(15000)
        .execute()) {
      log.info("url={}, status={}, body={}", url, httpResponse.getStatus(), httpResponse.body());
      if (!httpResponse.isOk()) {
        throw new ThirdAPIException("获取天气信息失败");
      }
      GaodeWeatherResp respBody = null;
      try {
        respBody = objectMapper.readValue(httpResponse.body(), GaodeWeatherResp.class);
      } catch (JsonProcessingException e) {
        throw new ThirdAPIException("解析天气信息失败", e);
      }
      if(!respBody.success() || CollectionUtil.isEmpty(respBody.getForecasts())){
        throw new ThirdAPIException("解析天气信息失败");
      }
      GaodeWeatherResp.Info first = respBody.first();
      if(CollectionUtil.isEmpty(first.getCasts())){
        throw new ThirdAPIException("解析天气信息失败");
      }

      Weather currentDay = first.getCasts().get(0);
      WeatherResp result = new WeatherResp();
      result.setLocation(Stream.of(first.getProvince(), first.getCity())
          .filter(Objects::nonNull)
          .collect(Collectors.joining("/")));
      result.setDate(currentDay.getDate());

      result.setDescription(Stream.of(currentDay.getDayweather(), currentDay.getNightweather())
          .filter(Objects::nonNull)
          .collect(Collectors.joining("/")));

      Double nightT = ParseUtils.parseTemp(currentDay.getNighttemp());
      Double dayT = ParseUtils.parseTemp(currentDay.getDaytemp());
      if(nightT == null || dayT == null){
        result.setLowerT(currentDay.getNighttemp());
        result.setHighT(currentDay.getDaytemp());
      }else{
        if(nightT.compareTo(dayT) > 0){
          result.setLowerT(dayT+"°C");
          result.setHighT(nightT+"°C");
        }else{
          result.setHighT(dayT+"°C");
          result.setLowerT(nightT+"°C");
        }
      }
      return result;
    }
  }

  /**
   查询adcode：https://lbs.amap.com/api/webservice/guide/api/district
      GET https://restapi.amap.com/v3/config/district
   免费：5000次/天
   */
  @Override
  public GaoDeAdcodeResp getAdCode(String area) {
    UrlBuilder urlBuilder = UrlBuilder.of("https://restapi.amap.com/v3/config/district");
    urlBuilder.addQuery("key", apiKey);
    urlBuilder.addQuery("keywords", area);
    urlBuilder.addQuery("subdistrict", "0");
    String url = urlBuilder.build();
    try (HttpResponse httpResponse = HttpUtil.createGet(url)
        .timeout(15000)
        .execute()) {
      log.info("url={}, status={}, body={}", url, httpResponse.getStatus(), httpResponse.body());
      if (!httpResponse.isOk()) {
        throw new ThirdAPIException("获取区域adcode信息失败");
      }
      GaoDeAdcodeResp respBody = null;
      try {
        respBody = objectMapper.readValue(httpResponse.body(), GaoDeAdcodeResp.class);
      } catch (JsonProcessingException e) {
        throw new ThirdAPIException("获取区域adcode信息失败", e);
      }
      if(!respBody.success()){
        throw new ThirdAPIException("获取区域adcode信息失败");
      }
      if(CollectionUtil.isEmpty(respBody.getDistricts())){
        throw new ThirdAPIException("获取区域adcode信息失败");
      }

      Location first = respBody.first();
      if(StringUtils.isBlank(first.getAdcode())){
        throw new ThirdAPIException("获取区域adcode信息失败");
      }
      if(StringUtils.isBlank(first.getName())){
        throw new ThirdAPIException("获取区域adcode信息失败，无区域名");
      }
      return respBody;
    }
  }
}
