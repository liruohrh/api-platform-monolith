package io.github.liruohrh.apiplatform.model.vo;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 
 * @TableName comment
 */
@Data
public class CommentVo implements Serializable {
    private Long id;
    private Long apiId;
    private Long userId;
    private String userNickName;
    private String username;

    private Integer score;
    private String content;
    private Long ctime;

    private Long replyToCommentId;
    private Long replyToUserId;
    private String replyToUserNickname;
    private String replyToUsername;
    private Long rootCommentId;

    private Integer favorCount;
    private Integer replyCount;

    private Boolean isFavor;

    @SchemaProperty(array= @ArraySchema(schema = @Schema(implementation = CommentVo.class)))
    private List<CommentVo> subCommentList;
    private static final long serialVersionUID = 1L;
}