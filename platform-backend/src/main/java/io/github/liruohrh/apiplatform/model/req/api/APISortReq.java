package io.github.liruohrh.apiplatform.model.req.api;

import io.github.liruohrh.apiplatform.model.enume.SortEnum;
import java.io.Serializable;
import lombok.Data;

@Data
public class APISortReq implements Serializable {
  private SortEnum price;
  private SortEnum orderVolume;
  private SortEnum score;
  private SortEnum ctime;
  private SortEnum utime;
  private static final long serialVersionUID = 1L;
}
