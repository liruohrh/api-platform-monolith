package io.github.liruohrh.apiplatform.model.vo;

import java.util.List;
import lombok.Data;
/**
 * amount、total
 * 指定年，就是这一年的总；
 * 指定年、月，就是这一月的总；
 * 否则就是所有的总
 */
@Data
public class ApiOrderStatisticsVo {
  private List<ApiOrderStatisticsResp> status;
  private Integer amount;
  private Double total;
}
