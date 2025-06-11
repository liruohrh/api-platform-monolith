package io.github.liruohrh.apiplatform.model.vo;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName comment
 */
@Data
public class CommentReplyVo implements Serializable {

    /**
     * 回复用户的回复
     */
    private Long id;
    private Long apiId;
    private Long userId;
    private String userNickname;
    private String apiName;
    private String content;
    private Long ctime;
    private Integer favorCount;
    private Integer replyCount;

    /**
     * 这个是用户评论
     */
    private CommentVo userComment;

    private Boolean isRead;
    private Boolean isFavor;
    private static final long serialVersionUID = 1L;
}