package io.github.liruohrh.apiplatform.model.req;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommonPageReq implements Serializable {
  @NotNull(message = "require current")
  private Integer current;
  private Integer size;
  private static final long serialVersionUID = 1L;
  public void ifNeedResetCurrent(){
    if(current == null || current <= 0){
      current = 1;
    }
  }
}
