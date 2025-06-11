package io.github.liruohrh.apiplatform.model.vo.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;

@Data
public class UserApiUsageVo  implements Serializable {
  @TableId(type = IdType.AUTO)
  private Long id;
  private Boolean isFreeApi;
  private Long apiId;
  private String apiName;
  private Long userId;
  private String username;
  private String userNickname;
  private Integer leftTimes;
  private Boolean freeUsed;
  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}
