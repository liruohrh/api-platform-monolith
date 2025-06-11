package io.github.liruohrh.apiplatform.model.req.order;

import java.io.Serializable;
import lombok.Data;

@Data
public class OrderSortReq implements Serializable {
  private Boolean actualPayment;
  private Boolean amount;
  private Boolean ctimeS;
  private Boolean utimeS;
  private static final long serialVersionUID = 1L;
}
