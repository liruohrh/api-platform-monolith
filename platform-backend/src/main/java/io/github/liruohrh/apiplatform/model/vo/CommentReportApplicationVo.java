package io.github.liruohrh.apiplatform.model.vo;

import java.io.Serializable;
import lombok.Data;
@Data
public class CommentReportApplicationVo implements Serializable {
    private Long id;
    private Integer applicationType;
    private String reason;
    private String description;
    private String replyContent;
    private Long apiId;
    private String apiName;
    /**
     * 根据不同申请类型做出不同审核状态回调，0-待审核，1-审核通过，2-审核不通过
     */
    private Integer auditStatus;
    private CommentVo  reportedComment;
    private Long reporterId;
    private String  reporterNickname;
    private String  reporterUsername;
    private Long ctime;
    private Long utime;
    private static final long serialVersionUID = 1L;
}