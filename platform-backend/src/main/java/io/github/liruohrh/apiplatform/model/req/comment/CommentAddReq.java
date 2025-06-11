package io.github.liruohrh.apiplatform.model.req.comment;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentAddReq implements Serializable {
  @NotNull(message = "apiId 不能为空")
  private Long apiId;
  @NotNull(message = "orderId 不能为空")
  private Long orderId;
  @NotNull(message = "score 不能为空")
  private Integer score;
  @NotEmpty(message = "评论内容不能为空")
  @NotNull(message = "评论内容不能为空")
  private String content;
  private static final long serialVersionUID = 1L;
}
