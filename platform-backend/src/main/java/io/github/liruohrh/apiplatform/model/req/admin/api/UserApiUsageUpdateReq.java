package io.github.liruohrh.apiplatform.model.req.admin.api;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserApiUsageUpdateReq implements Serializable {
  private Integer leftTimes;
  private Boolean freeUsed;
  private static final long serialVersionUID = 1L;
}
