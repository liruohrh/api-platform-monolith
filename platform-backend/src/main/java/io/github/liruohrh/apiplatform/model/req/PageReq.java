package io.github.liruohrh.apiplatform.model.req;

import java.io.Serializable;
import lombok.Data;

@Data
public class PageReq<T, S> implements Serializable {
  private T search;
  private Integer current;
  private Integer size;
  private S sort;
  private Boolean isFree;
  private static final long serialVersionUID = 1L;
}
