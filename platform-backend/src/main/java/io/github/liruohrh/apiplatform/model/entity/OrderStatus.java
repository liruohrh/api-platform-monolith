package io.github.liruohrh.apiplatform.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

/**
 * 
 * @TableName order_status
 */
@TableName(value ="order_status")
@Data
public class OrderStatus implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private LocalDate date;

    /**
     * 
     */
    private Long apiId;

    /**
     * 当日总金额
     */
    private Double amount;

    /**
     * 当日总交易量
     */
    private Integer total;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}