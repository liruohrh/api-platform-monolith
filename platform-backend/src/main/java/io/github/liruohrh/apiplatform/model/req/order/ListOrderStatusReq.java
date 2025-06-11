package io.github.liruohrh.apiplatform.model.req.order;

import java.util.List;
import lombok.Data;

@Data
public class ListOrderStatusReq {
  private Integer current;
  private Boolean isMonth;
  private List<Long> dateRange;

  private Boolean amountS;
  private Boolean totalS;
}
