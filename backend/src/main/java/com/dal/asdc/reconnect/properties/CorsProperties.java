package com.dal.asdc.reconnect.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CorsProperties {
    
    @Value("${cors.allowed.origins}")
    private String allowedOrigins;

}
