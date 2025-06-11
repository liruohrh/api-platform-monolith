package io.github.liruohrh.apiplatform.model.vo;

import io.github.liruohrh.apiplatform.model.entity.Application;
import io.github.liruohrh.apiplatform.model.resp.api.HttpApiResp;
import java.io.Serializable;
import lombok.Data;

@Data
public class OrderVo implements Serializable {
    private Long id;

    /**
     * 
     */
    private String orderId;

    /**
     * 
     */
    private Long apiId;
    private HttpApiResp api;
    /**
     *
     */
    private String apiName;

    /**
     * 
     */
    private Long userId;
    private String username;
    private String userNickname;

    /**
     * 
     */
    private Double actualPayment;

    /**
     * 
     */
    private Integer amount;
    private Boolean isUsed;

    private Integer status;
    private Application application;
    private Double price;
    private Boolean isComment;

    private Long ctime;
    private Long utime;

    private static final long serialVersionUID = 1L;
}