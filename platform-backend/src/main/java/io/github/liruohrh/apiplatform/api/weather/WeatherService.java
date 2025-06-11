package io.github.liruohrh.apiplatform.api.weather;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpException;
import io.github.liruohrh.apiplatform.apicommon.error.BusinessException;
import io.github.liruohrh.apiplatform.apicommon.error.ThirdAPIException;
import io.github.liruohrh.apiplatform.apicommon.req.WeatherReq;
import io.github.liruohrh.apiplatform.apicommon.resp.WeatherResp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WeatherService {
  @Resource
  List<WeatherHandler> weatherHandlerList;
  public WeatherResp getWeather(WeatherReq req) {
    Set<String> errorList = new HashSet<>(weatherHandlerList.size());
    ArrayList<Exception> errors = new ArrayList<>(weatherHandlerList.size());
    String uuid = UUID.randomUUID().toString();
    for (WeatherHandler weatherHandler : weatherHandlerList) {
      try {
        if(!weatherHandler.canHandler(req)){
          continue;
        }
        WeatherResp result = weatherHandler.getWeather(req);
        for (int i = 0; i < errors.size(); i++) {
          Exception error = errors.get(i);
          log.warn("[{}] [{}/{}] Fail to get weather", uuid, i+1, errors.size(), error);
        }
        return result;
      } catch (IORuntimeException | HttpException | BusinessException e) {
        errors.add(e);
        errorList.add(e.getMessage());
      }
    }


    for (Exception error : errors) {
      log.warn("[{}], Fail to get weather", uuid, error);
    }
    throw new ThirdAPIException("获取天气失败。"+ String.join(", ", errorList));
  }
}
