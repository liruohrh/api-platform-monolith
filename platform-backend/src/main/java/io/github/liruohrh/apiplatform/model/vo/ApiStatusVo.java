package io.github.liruohrh.apiplatform.model.vo;

import io.github.liruohrh.apiplatform.model.entity.ApiStatus;
import java.util.List;
import lombok.Data;

@Data
public class ApiStatusVo {
  private List<ApiStatus> status;
  private Integer callTimes;
  private Integer successTimes;
  private Long totalDuration;
}
