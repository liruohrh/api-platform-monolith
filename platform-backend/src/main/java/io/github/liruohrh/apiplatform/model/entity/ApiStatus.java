package io.github.liruohrh.apiplatform.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName api_status
 */
@TableName(value ="api_status")
@Data
public class ApiStatus implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String createDate;

    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Long apiId;

    /**
     * 
     */
    private Integer callTimes;

    /**
     * 
     */
    private Integer successTimes;

    /**
     * 总耗时，单位：ms
     */
    private Long totalDuration;
    public static ApiStatus of(String date){
        ApiStatus apiStatus = new ApiStatus();
        apiStatus.setCreateDate(date);

        apiStatus.setCallTimes(0);
        apiStatus.setSuccessTimes(0);
        apiStatus.setTotalDuration(0L);
        return apiStatus;
    }

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}