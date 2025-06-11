package io.github.liruohrh.apiplatform.model.req.application;

import java.util.List;
import lombok.Data;

@Data
public class ListApplicationReq {
  private Integer current;

  private String reporterName;
  private String reason;
  private Integer auditStatus;
  private List<Long> ctimeRange;
  private Boolean ctimeS;
}
