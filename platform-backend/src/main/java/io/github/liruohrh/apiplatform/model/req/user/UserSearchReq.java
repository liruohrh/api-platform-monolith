package io.github.liruohrh.apiplatform.model.req.user;

import java.util.List;
import lombok.Data;

@Data
public class UserSearchReq {
  private Integer current;

  private Long id;
  private String nickname;
  private String username;
  private String email;
  private String personalDescription;
  private String appKey;
  private String role;
  private Integer status;
  private List<Long> ctime;

  private Boolean ctimeS;
}
