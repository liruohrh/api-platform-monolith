package io.github.liruohrh.apiplatform.model.req.user;

import io.github.liruohrh.apiplatform.model.enume.SortEnum;
import java.io.Serializable;
import lombok.Data;

@Data
public class UserSortReq implements Serializable {
  private SortEnum ctime;
  private static final long serialVersionUID = 1L;
}
