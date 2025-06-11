package io.github.liruohrh.apiplatform.model.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class CommentPageVo implements Serializable {
  private Integer score;
  private String apiName;
  private String username;
  private String userNickname;
  private String content;
  private Long ctime;
  private Integer favorCount;
  private Integer replyCount;

  private Long id;
  private Long apiId;
  private Long userId;

  private Boolean isRoot;

  private List<CommentVo> adminReplies;
  private static final long serialVersionUID = 1L;
}
