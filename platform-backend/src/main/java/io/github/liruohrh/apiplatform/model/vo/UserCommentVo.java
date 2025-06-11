package io.github.liruohrh.apiplatform.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName comment
 */
@Data
public class UserCommentVo implements Serializable {
    private Long id;
    private Long apiId;
    private String apiName;
    private Integer score;
    private String content;
    private Long ctime;
    private Long rootCommentId;
    private Integer favorCount;
    private Integer replyCount;


    private Boolean isReply;
    private String addresseeNickname;
    @Schema(description = "回复的对象的评论，可能被删除了")
    private CommentVo addresseeComment;
    private static final long serialVersionUID = 1L;
}