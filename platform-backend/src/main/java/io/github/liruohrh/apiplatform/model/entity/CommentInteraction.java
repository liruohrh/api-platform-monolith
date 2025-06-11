package io.github.liruohrh.apiplatform.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName comment_interaction
 */
@TableName(value ="comment_interaction")
@Data
public class CommentInteraction implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long commentId;

    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Boolean favor;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}