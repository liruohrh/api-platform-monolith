package io.github.liruohrh.apiplatform.api.model;

import lombok.Data;

@Data
public class VVHanWeatherReq {
    private String city;
    private String ip;
    private String type;
}