package io.github.liruohrh.apiplatform.model.req.api;

import io.github.liruohrh.apiplatform.model.enume.SortEnum;
import java.io.Serializable;
import lombok.Data;

@Data
public class ApiPurchasedSortReq implements Serializable {
  private SortEnum price;
  private SortEnum leftTimes;
  public APISortReq toAPISortReq(){
    APISortReq apiSortReq = new APISortReq();
    apiSortReq.setPrice(price);
    return apiSortReq;
  }
  private static final long serialVersionUID = 1L;
}
