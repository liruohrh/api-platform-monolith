package io.github.liruohrh.apiplatform.model.req.admin.api;

import java.util.List;
import lombok.Data;

@Data
public class AdminApiSearchReq {
  private Integer current;

  private Boolean score;
  private Boolean orderVolume;
  private Boolean ctimeS;
  private Boolean utimeS;

  private Long id;
  private String name;
  private String description;
  private String method;
  private String protocol;
  private String domain;
  private String path;
  private Integer status;
  private List<Long> ctime;
  private List<Long> utime;
}
