package io.github.liruohrh.apiplatform.model.req.api;

import io.github.liruohrh.apiplatform.common.validator.FloatFractional;
import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.http.HttpMethod;

@Data
public class HttpApiUpdateReq implements Serializable {
    @NotNull(message = "不知道更新哪个API")
    private Long id;


    private String description;


    private String logoUrl;
    private String name;
    private Integer status;


    @Pattern(regexp = "(^((\\d{1,3}\\.){3}\\d{1,3})|^((\\w+\\.)+\\w+))(:\\d{1,5})?$", message = "非法domain")
    private String domain;

    @NotEmpty(message = "协议不能为空")
    private String protocol;
    @Pattern(regexp = "^/[\\w/]+", message = "非法API path")
    @NotEmpty(message = "API path不能为空")
    private String path;

    private HttpMethod method;
    private String params;
    private String reqHeaders;
    private String reqBody;
    private String respHeaders;
    private String respBody;
    private String respSuccess;
    private String respFail;
    private String errorCodes;
    /**
     * 最多3位小数
     * 空表示免费
     */
    @FloatFractional(max=3, message = "API价格小数位不超过3")
    private Double price;

    private Integer freeTimes;
    private static final long serialVersionUID = 1L;
}