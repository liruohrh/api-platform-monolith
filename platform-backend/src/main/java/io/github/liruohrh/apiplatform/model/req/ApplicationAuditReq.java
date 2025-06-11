package io.github.liruohrh.apiplatform.model.req;

import io.github.liruohrh.apiplatform.model.enume.ApplicationAuditStatus;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 
 */
@Data
public class ApplicationAuditReq implements Serializable {
    private String replyContent;
    @NotNull(message = "require auditStatus")
    private ApplicationAuditStatus auditStatus;
    private static final long serialVersionUID = 1L;
}