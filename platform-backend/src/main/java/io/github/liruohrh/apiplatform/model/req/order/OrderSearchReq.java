package io.github.liruohrh.apiplatform.model.req.order;

import io.github.liruohrh.apiplatform.model.OrderNumber;
import java.io.Serializable;
import lombok.Data;
@Data
public class OrderSearchReq implements Serializable {
    private String orderId;
    private Long apiId;
    private Long userId;
    private OrderNumber actualPayment;
    private String apiName;

    /**
     * 0-待支付，1-已支付，2-已取消，3-申请退款中，4-退款成功，5-退款失败
     */
    private Integer status;
    private Float price;
    private OrderNumber ctime;
    private OrderNumber utime;
    private static final long serialVersionUID = 1L;
}