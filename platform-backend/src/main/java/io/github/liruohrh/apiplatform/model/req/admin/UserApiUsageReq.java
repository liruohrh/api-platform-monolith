package io.github.liruohrh.apiplatform.model.req.admin;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserApiUsageReq implements Serializable {
    private String username;
    private String apiName;
    private Boolean excludeFreeApi;
    private Integer current;
    private static final long serialVersionUID = 1L;
}
