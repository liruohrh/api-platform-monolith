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
 * @TableName http_api
 */
@TableName(value ="http_api")
@Data
public class HttpApi implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerId;



    /**
     * 
     */
    private String description;


    /**
     * 
     */
    private String name;
    private String logoUrl;

    /**
     * 
     */
    private String protocol;
    private String domain;

    private String path;

    /**
     * 大写
     */
    private String method;

    /**
     * 
     */
    private String params;

    /**
     * 
     */
    private String reqHeaders;

    /**
     * 
     */
    private String reqBody;

    /**
     * 
     */
    private String respHeaders;

    /**
     * 
     */
    private String respBody;
    private String respSuccess;
    private String respFail;
    private String errorCodes;


    /**
     * 最多3位小数
     */
    private Double price;

    /**
     * 
     */
    private Integer freeTimes;

    private Integer score;
    private Integer orderVolume;

    /**
     * 0-待审核，1-上线，2-下线，3-被禁用
     */
    private Integer status;

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