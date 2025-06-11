package io.github.liruohrh.apiplatform.client;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.apicommon.resp.WeatherResp;
import io.github.liruohrh.apiplatform.apicommon.utils.SignUtils;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LApiClient {

  private final String appKey;
  private final String appSecret;
  public final String baseUrl = "http://127.0.0.1:9000/api-platform";
  public LApiClient(String appKey, String appSecret) {
    this.appKey = appKey;
    this.appSecret = appSecret;
  }

  public Map<String, String> getSignHeaderMap() {
    HashMap<String, String> map = new HashMap<>();
    map.put(SignUtils.KEY_APP_KEY, appKey);
    SecureRandom secureRandom = new SecureRandom();
    String nonce = HexUtil.encodeHexStr(secureRandom.generateSeed(16));
    map.put(SignUtils.KEY_NONCE, nonce);
    String timestamp  = System.currentTimeMillis() + "";
    map.put(SignUtils.KEY_TIMESTAMP, timestamp);
    String extra = HexUtil.encodeHexStr(secureRandom.generateSeed(32));
    map.put(SignUtils.KEY_EXTRA, extra);

    map.put(SignUtils.KEY_SIGN, SignUtils.generateSign(
        appKey, appSecret, nonce, timestamp, extra
    ));
    return map;
  }


  public Resp<WeatherResp> getWeather(String city, String ip, String adcode){
    HashMap<String, Object> paramMap = new HashMap<>();
    if(StrUtil.isNotEmpty(city)){
      paramMap.put("city", city);
    }
    if(StrUtil.isNotEmpty(ip)){
      paramMap.put("ip", ip);
    }
    if(StrUtil.isNotEmpty(adcode)){
      paramMap.put("adcode", adcode);
    }
    HttpResponse response = HttpRequest.get(baseUrl
            + "/api/web/weather"
            +( paramMap.isEmpty() ? "" : "?")
            + paramMap.entrySet().stream()
            .map(param -> param.getKey() + "=" + param.getValue())
            .collect(Collectors.joining("&"))
        )
        .addHeaders(getSignHeaderMap())
        .execute();
    return JSONUtil.toBean(response.body(), new TypeReference<Resp<WeatherResp>>() {}, false);
  }

  public Resp<String> getColdJoke(String description){
    HashMap<String, Object> paramMap = new HashMap<>();
    if(StrUtil.isNotEmpty(description)){
      paramMap.put("description", description);
    }
    HttpResponse response = HttpRequest.post(baseUrl
            + "/api/web/cold-joke"
            +( paramMap.isEmpty() ? "" : "?")
            + paramMap.entrySet().stream()
            .map(param -> param.getKey() + "=" + param.getValue())
            .collect(Collectors.joining("&"))
        )
        .addHeaders(getSignHeaderMap())
        .execute();
    return JSONUtil.toBean(response.body(), new TypeReference<Resp<String>>() {}, false);
  }
}
