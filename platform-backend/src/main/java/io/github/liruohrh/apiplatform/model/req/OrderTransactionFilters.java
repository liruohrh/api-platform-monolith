package io.github.liruohrh.apiplatform.model.req;

import lombok.Data;

@Data
public class OrderTransactionFilters {
    private String apiName;
    private String searchType;
    private String date;
}
