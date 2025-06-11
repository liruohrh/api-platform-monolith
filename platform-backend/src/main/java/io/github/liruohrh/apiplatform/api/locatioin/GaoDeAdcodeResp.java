package io.github.liruohrh.apiplatform.api.locatioin;

import java.util.List;
import lombok.Data;

@Data
public class GaoDeAdcodeResp {
  //1：成功；0：失败
  private String status;
  //返回状态说明,10000代表正确
  private String infocode;
  public boolean success(){
    return "1".equals(status) && "10000".equals(infocode);
  }
  public Location first(){
    return districts.get(0);
  }
  private List<Location> districts;
  @Data
  public static class Location{
    private String name;
    private String adcode;
  }
}
