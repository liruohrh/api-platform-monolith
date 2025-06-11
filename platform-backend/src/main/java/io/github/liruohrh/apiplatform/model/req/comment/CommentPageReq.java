package io.github.liruohrh.apiplatform.model.req.comment;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentPageReq implements Serializable {
  @NotNull(message = "分页信息不能为空")
  private Integer current;
  private Boolean ctime;
  private Boolean favorCount;
  private Boolean replyCount;
  private Long rootCommentId;
  private Boolean isUserReply;
  private String username;
  private Long apiId;
  private Long replyToCommentId;
  private Boolean replyToMe;
  private Boolean isRoot;
  private Boolean excludeMe;
  private static final long serialVersionUID = 1L;
}
