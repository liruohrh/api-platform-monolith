package io.github.liruohrh.apiplatform.model.req.api;

import io.github.liruohrh.apiplatform.common.validator.FloatFractional;
import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import org.springframework.http.HttpMethod;

@Data
public class HttpApiAddReq implements Serializable {


    @Size(max = 1024, message = "API描述长度最多1024")
    @NotEmpty(message = "API描述不能为空")
    private String description;
    private String logoUrl;
    @NotEmpty(message = "API名称不能为空")
    private String name;


    @Pattern(regexp = "(^((\\d{1,3}\\.){3}\\d{1,3})|^((\\w+\\.)+\\w+))(:\\d{1,5})?$", message = "非法domain")
    private String domain;
    @NotEmpty(message = "协议不能为空")
    private String protocol;
    @Pattern(regexp = "^/[\\w/]+", message = "非法API path")
    @NotEmpty(message = "API path不能为空")
    private String path;

    @NotEmpty(message = "API非法不能为空")
    private HttpMethod method;



//没必要检查，因为请求是直接转发的，如果别人不是通过前端添加，格式有问题，那也没什么
    /*
{
 "name": {
     "required": true,
     "type": "string",
     "desc": "xxx",
  }
}
     */
    private String params;
    /*
{
 "name": {
     "required": true,
     "desc": "xxx",
  }
}
     */
    private String reqHeaders;

    /*
{
 "name": {
     "required": true,
     "desc": "xxx",
     "type": "string",
  }
}
     */
    private String reqBody;

    /*
{
 "name": {
     "required": true,
     "desc": "xxx",
  }
}
     */
    private String respHeaders;

    /*
{
"name": {
    "options": true,
    "desc": "xxx",
    "type": "string",
 }
}
    */
    private String respBody;

    private String respSuccess;
    private String respFail;
    /*
{
    "errorCode": {
        "httpStatus": 400,
        "desc": "",
    }
}
*/
    private String errorCodes;

    /**
     * 最多3位小数
     * 0表示免费
     */
    @FloatFractional(max=3, message = "API价格小数位不超过3")
    @NotNull(message = "价格不能为空，如果免费，请填0")
    private Double price;
    /**
     * 如果是免费，不需要填，此时该值无意义
     */
    private Integer freeTimes;

    private static final long serialVersionUID = 1L;
}