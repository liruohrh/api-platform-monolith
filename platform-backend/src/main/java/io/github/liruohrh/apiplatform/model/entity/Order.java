package io.github.liruohrh.apiplatform.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName order
 */
@TableName(value ="goods_order")
@Data
public class Order implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String orderId;

    /**
     * 
     */
    private Long apiId;

    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Double actualPayment;

    /**
     * 
     */
    private Integer amount;

    /**
     * 0-待支付，1-已支付，2-已取消，3-申请退款中，4-退款成功，5-退款失败
     */
    private Integer status;
    private Double price;
    private Boolean isComment;

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

    /**
     * 是否使用了
     */
    private Boolean isUsed;

    /**
     * 
     */
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}