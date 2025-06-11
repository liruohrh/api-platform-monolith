package io.github.liruohrh.apiplatform.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName application
 */
@TableName(value ="application")
@Data
public class Application implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long reporterId;

    /**
     * 申请类型
     */
    private Integer applicationType;

    /**
     * 
     */
    private String reason;

    /**
     * 
     */
    private String description;

    /**
     * 
     */
    private String replyContent;

    /**
     * 根据不同申请类型做出不同审核状态回调，0-待审核，1-审核通过，2-审核不通过
     */
    private Integer auditStatus;
    private String  extraData;
    /**
     * 
     */
    @TableField(fill = FieldFill.INSERT)
    private Long ctime;

    /**
     * 
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long utime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}