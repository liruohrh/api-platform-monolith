package io.github.liruohrh.apiplatform.api.controller;

import io.github.liruohrh.apiplatform.api.weather.WeatherService;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.apicommon.req.WeatherReq;
import io.github.liruohrh.apiplatform.apicommon.resp.WeatherResp;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/web/weather")
@RestController
public class WeatherController {
  @Resource
  private WeatherService weatherService;
  @GetMapping
  public Resp<WeatherResp> currentWeekWeather(
      WeatherReq req
  ) {
      return Resp.ok(weatherService.getWeather(req));
  }
}
