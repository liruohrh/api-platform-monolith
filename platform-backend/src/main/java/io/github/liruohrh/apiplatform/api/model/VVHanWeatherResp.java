package io.github.liruohrh.apiplatform.api.model;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.liruohrh.apiplatform.apicommon.vo.AirQualityVo;
import io.github.liruohrh.apiplatform.apicommon.vo.WeatherInfoVo;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class VVHanWeatherResp {

    private boolean success;
    private String city;

    @JsonProperty("data")
    private List<Weather> weather;

    @JsonProperty("air")
    private AirQuality airQuality;


    @Data
    public static class Weather {
        private LocalDate date;
        private String week;
        private String type;
        private String low;
        private String high;
        private String fengxiang;
        private String fengli;
        @JsonProperty("night")
        private Weather night;
        public WeatherInfoVo to(){
            WeatherInfoVo weatherInfoVo = BeanUtil.copyProperties(this, WeatherInfoVo.class);
            weatherInfoVo.setLowDegreesCelsius(this.low);
            weatherInfoVo.setHighDegreesCelsius(this.high);
            weatherInfoVo.setWindDirection(this.fengxiang);
            weatherInfoVo.setWindForce(this.fengli);
            if(this.night != null){
                weatherInfoVo.setNight(this.night.to());
            }
            return weatherInfoVo;
        }
    }

    @Data
    public static class AirQuality {
        private int aqi;
        private int aqiLevel;
        private String aqiName;
        private String co;
        private String no2;
        private String o3;
        private String pm10;
        private String pm25;
        private String so2;
        public AirQualityVo to(){
            return BeanUtil.copyProperties(this, AirQualityVo.class);
        }
    }
}