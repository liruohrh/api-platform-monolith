package io.github.liruohrh.apiplatform.client;

import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.apicommon.resp.WeatherResp;
import org.junit.jupiter.api.Test;

public class ApiClientTest {
  @Test
  public void testColdJoke() {
    LApiClient client = new LApiClient("d5634f669f976deca5d784fd6e44cadb",
        "90276b180c9397818c50c759676ac09e");
    Resp<String> resp = client.getColdJoke("与封神大战有关的");
    System.out.println(resp);
  }
  @Test
  public void testWeather() {
    LApiClient client = new LApiClient("001e8207e6d7eda1fb7c4f4c8cf58db7",
        "48d9d3799e31bc4773f3787bfc5d265d");
    Resp<WeatherResp> resp = client.getWeather("惠州", null, null);
    System.out.println(resp);
  }
}
