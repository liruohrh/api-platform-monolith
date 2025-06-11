package io.github.liruohrh.apiplatform.api.weather;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.liruohrh.apiplatform.apicommon.req.WeatherReq;
import io.github.liruohrh.apiplatform.apicommon.resp.WeatherResp;

public interface WeatherHandler {
   ObjectMapper objectMapper = new ObjectMapper()
       .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
   boolean canHandler(WeatherReq req);
   WeatherResp getWeather(WeatherReq req);
}
