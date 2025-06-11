package io.github.liruohrh.apiplatform.model.req.order;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRefundReq {
  @NotEmpty(message = "必须要有订单编号")
  @NotNull(message = "必须要有订单编号")
  private String orderId;
  @NotEmpty(message = "必须要有原因")
  @NotNull(message = "必须要有原因")
  private String reason;
  private String description;
}
