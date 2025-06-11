package io.github.liruohrh.apiplatform.model.vo;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserVo implements Serializable {
    /**
     *
     */
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
    private String email;

    /**
     * 
     */
    private String personalDescription;

    /**
     * 
     */
    private String avatarUrl;
    private String appKey;


    /**
     * 
     */
    private String role;

    /**
     * 0-正常，1-冻结用户，2-冻结服务商，3-冻结用户+服务商
     */
    private Integer status;

    private Long ctime;

    private static final long serialVersionUID = 1L;
}