package io.github.liruohrh.apiplatform.model.req.comment;

import java.io.Serializable;
import lombok.Data;

@Data
public class CommentReplyReq implements Serializable {
  private Long apiId;
  private Long replyToCommentId;
  private String content;
  private static final long serialVersionUID = 1L;
}
