package io.github.liruohrh.apiplatform.model.req.api;

import io.github.liruohrh.apiplatform.model.enume.SortEnum;
import java.io.Serializable;
import lombok.Data;

@Data
public class ApiSearchSortReq implements Serializable {
  private SortEnum price;
  private SortEnum orderVolume;
  private SortEnum score;
  private SortEnum ctime;
  public APISortReq toAPISortReq(){
    APISortReq apiSortReq = new APISortReq();
    apiSortReq.setPrice(price);
    apiSortReq.setOrderVolume(orderVolume);
    apiSortReq.setScore(score);
    apiSortReq.setCtime(ctime);
    apiSortReq.setUtime(null);
    return apiSortReq;
  }
  private static final long serialVersionUID = 1L;
}
