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
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String nickname;

    /**
     * 
     */
    private String username;

    /**
     * 
     */
    private String passwd;

    /**
     * 
     */
    private String email;

    /**
     * 
     */
    private String personalDescription;

    /**
     * 
     */
    private String avatarUrl;

    /**
     * 
     */
    private String appKey;

    /**
     * 
     */
    private String appSecret;

    /**
     * 所有API的前缀，默认username
     */
    private String apiPrefix;

    /**
     * 
     */
    private String role;

    /**
     * 0-正常，1-冻结用户，2-冻结服务商，3-冻结用户+服务商
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
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}