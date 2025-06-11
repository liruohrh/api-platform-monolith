package io.github.liruohrh.apiplatform.model.req.order;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class OrderCreateReq implements Serializable {
    @NotNull(message = "未知API")
    private Long apiId;
    @Range(min = 1,message = "最少需要购买1个使用次数")
    private Integer amount;
    private Boolean free;
    private static final long serialVersionUID = 1L;
}