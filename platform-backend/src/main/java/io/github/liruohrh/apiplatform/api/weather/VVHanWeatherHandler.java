package io.github.liruohrh.apiplatform.api.weather;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.liruohrh.apiplatform.api.weather.model.VVHanWeatherResp;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.apicommon.error.ThirdAPIException;
import io.github.liruohrh.apiplatform.apicommon.error.TimeoutException;
import io.github.liruohrh.apiplatform.apicommon.req.WeatherReq;
import io.github.liruohrh.apiplatform.apicommon.resp.WeatherResp;
import io.github.liruohrh.apiplatform.apicommon.utils.ServletUtils;
import io.github.liruohrh.apiplatform.apicommon.utils.ValidUtils;
import java.net.URL;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(200)
@Slf4j
@Component
public class VVHanWeatherHandler implements WeatherHandler {

  @Override
  public boolean canHandler(WeatherReq req) {
    if(StringUtils.isAllEmpty(req.getCity(), req.getIp())){
      //不考虑代理
      String remoteAddr = ServletUtils.getRequest().getRemoteAddr();
      if(!remoteAddr.equals("127.0.0.1") && !remoteAddr.equals("localhost") && !remoteAddr.startsWith("192.168.")){
        req.setIp(remoteAddr);
      }else{
        String ip = getIp();
        if(StringUtils.isBlank(ip) || !ValidUtils.isSocketAddr(ip)){
          ip = "111.40.76.47";
        }
        req.setIp(ip);
      }
    }
    return !StringUtils.isAllEmpty(req.getCity(), req.getIp());
  }
  static String getIp() {
    String ipQueryUrl = "https://ip.900cha.com";
    try {
      Document document = Jsoup.parse(new URL(ipQueryUrl), 5000);
      return document.select("h3.text-danger").text().trim();
    } catch (Exception e) {
      log.error("fail to get ip", e);
      return null;
    }
  }
  /*
     https://api.vvhan.com/api/weather
         GET https://api.vvhan.com/api/weather
           args:
            无：当前ip
            ip: optional
            city: optional
            type: optional, 默认一天，week则一周
     */
  @Override
  public WeatherResp getWeather(WeatherReq req) {
    if(StringUtils.isAllEmpty(req.getCity(), req.getIp())){
      throw new ParamException("city or ip is required");
    }

    if (StrUtil.isAllNotEmpty(req.getCity(), req.getIp())) {
      throw new ParamException("city和ip不能同时存在");
    }
    UrlBuilder urlBuilder = UrlBuilder.of("https://api.vvhan.com/api/weather");
    if (StrUtil.isNotEmpty(req.getCity())) {
      urlBuilder.addQuery("city", req.getCity());
    }else if (StrUtil.isNotEmpty(req.getIp())) {
      urlBuilder.addQuery("ip", req.getIp());
    }
    String url = urlBuilder.build();
    try (HttpResponse httpResponse = HttpUtil.createGet(url)
        .timeout(25000)
        .execute()) {
      log.info("url={}, status={}, body={}", url, httpResponse.getStatus(), httpResponse.body());
      if (!httpResponse.isOk()) {
        if(httpResponse.getStatus() == 504 || StringUtils.contains(httpResponse.body(), "超时")){
          throw new TimeoutException("获取天气信息失败");
        }
        throw new ThirdAPIException("获取天气信息失败");
      }
      VVHanWeatherResp respBody = null;
      try {
        respBody = objectMapper.readValue(httpResponse.body(), VVHanWeatherResp.class);
      } catch (JsonProcessingException e) {
        throw new ThirdAPIException("解析天气信息失败", e);
      }
      if(respBody == null || respBody.getData() == null){
        throw new ThirdAPIException("解析天气信息失败");
      }
      VVHanWeatherResp.Weather currentDay = respBody.getData();
      WeatherResp result = new WeatherResp();
      result.setLocation(respBody.getCity());
      result.setDate(currentDay.getDate());
      result.setDescription(
          Stream.of(currentDay.getType(), currentDay.getNightType())
              .filter(Objects::nonNull)
              .collect(Collectors.joining("/"))
      );
      result.setLowerT(currentDay.getLow());
      result.setHighT(currentDay.getHigh());
      return result;
    }
  }
}
