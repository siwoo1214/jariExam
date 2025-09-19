package com.parkingInfo.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "seoul.api")
public class SeoulApiConfig {
    // api 호출정보들
    private String key;
    private String baseUrl;
    private String serviceName;
    private String responseType;

}
