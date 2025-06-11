package io.github.liruohrh.apiplatform.api;

import io.github.liruohrh.apiplatform.api.weather.GaoDeWeatherHandler;
import io.github.liruohrh.apiplatform.api.weather.HeFengWeatherHandler;
import io.github.liruohrh.apiplatform.api.weather.VVHanWeatherHandler;
import io.github.liruohrh.apiplatform.api.weather.WeatherService;
import io.github.liruohrh.apiplatform.apicommon.req.WeatherReq;
import io.github.liruohrh.apiplatform.apicommon.resp.WeatherResp;
import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WeatherTest {
  @Resource
  WeatherService weatherService;

  @Resource
  GaoDeWeatherHandler gaoDeWeatherHandler;
  @Resource
  HeFengWeatherHandler heFengWeatherHandler;
  @Resource
  VVHanWeatherHandler vvHanWeatherHandler;
  @Test
  public void testGaoDe() {
    WeatherResp weather = gaoDeWeatherHandler.getWeather(WeatherReq.builder()
            .city("惠州")
        .build());
    System.out.println(weather);
    Assertions.assertNotNull(weather);
    Assertions.assertNotNull(weather.getDate());
    Assertions.assertNotNull(weather.getDescription());
    Assertions.assertNotNull(weather.getLowerT());
    Assertions.assertNotNull(weather.getHighT());
  }

  @Test
  public void testHeFeng() {
    WeatherResp weather = heFengWeatherHandler.getWeather(WeatherReq.builder()
        .city("惠州")
        .build());
    System.out.println(weather);
    Assertions.assertNotNull(weather);
    Assertions.assertNotNull(weather.getDate());
    Assertions.assertNotNull(weather.getDescription());
    Assertions.assertNotNull(weather.getLowerT());
    Assertions.assertNotNull(weather.getHighT());
  }

  @Test
  public void testVVHan() {
    WeatherResp weather = vvHanWeatherHandler.getWeather(WeatherReq.builder()
        .city("惠州")
        .build());
    System.out.println(weather);
    Assertions.assertNotNull(weather);
    Assertions.assertNotNull(weather.getDate());
    Assertions.assertNotNull(weather.getDescription());
    Assertions.assertNotNull(weather.getLowerT());
    Assertions.assertNotNull(weather.getHighT());
  }

  @Test
  public void testAdCode() {
    WeatherResp weather = weatherService.getWeather(WeatherReq.builder()
        .adcode("441300")
        .build());
    System.out.println(weather);
    Assertions.assertNotNull(weather);
    Assertions.assertNotNull(weather.getDate());
    Assertions.assertNotNull(weather.getDescription());
    Assertions.assertNotNull(weather.getLowerT());
    Assertions.assertNotNull(weather.getHighT());
  }

  @Test
  public void testIp() {
    WeatherResp r2 = weatherService.getWeather(WeatherReq.builder()
        .ip("223.74.196.26")
        .build());
    System.out.println(r2);
    Assertions.assertNotNull(r2);
    Assertions.assertNotNull(r2.getDate());
    Assertions.assertNotNull(r2.getDescription());
    Assertions.assertNotNull(r2.getLowerT());
    Assertions.assertNotNull(r2.getHighT());
  }
  @Test
  public void testCity() {
    WeatherResp r1 = weatherService.getWeather(WeatherReq.builder()
        .city("惠州")
        .build());
    System.out.println(r1);
    Assertions.assertNotNull(r1);
    Assertions.assertNotNull(r1.getDate());
    Assertions.assertNotNull(r1.getDescription());
    Assertions.assertNotNull(r1.getLowerT());
    Assertions.assertNotNull(r1.getHighT());
  }
}
