package io.github.liruohrh.apiplatform.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName api_call
 */
@TableName(value ="api_call")
@Data
public class ApiCall implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long apiId;

    /**
     * 
     */
    private Long callerId;

    /**
     * 0表示免费
     */
    private Integer leftTimes;

    /**
     * 付费接口是否使用了免费次数，与免费接口无关
     */
    private Boolean freeUsed;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}