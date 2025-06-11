package io.github.liruohrh.apiplatform.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName api_call_log
 */
@NoArgsConstructor
@AllArgsConstructor
@TableName(value ="api_call_log")
@Data
public class ApiCallLog implements Serializable {

    public ApiCallLog(Long apiId, Long callerId, Boolean success, Integer timeConsumingMs) {
        this.apiId = apiId;
        this.callerId = callerId;
        this.success = success;
        this.timeConsumingMs = timeConsumingMs;
    }

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
     * 0-失败，1-成功
     */
    private Boolean success;

    /**
     * 
     */
    private Integer timeConsumingMs;

    @TableField(fill = FieldFill.INSERT)
    private Long ctime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}