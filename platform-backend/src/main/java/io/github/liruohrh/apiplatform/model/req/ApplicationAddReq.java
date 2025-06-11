package io.github.liruohrh.apiplatform.model.req;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 
 */
@Data
public class ApplicationAddReq implements Serializable {
    @NotEmpty(message = "require title")
    @NotNull(message = "require title")
    private String reason;
    @NotEmpty(message = "require content")
    @NotNull(message = "require content")
    private String description;
    private static final long serialVersionUID = 1L;
}