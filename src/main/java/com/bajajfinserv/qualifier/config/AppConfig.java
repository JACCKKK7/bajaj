package com.bajajfinserv.qualifier.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    
    private User user = new User();
    private Api api = new Api();
    
    @Data
    public static class User {
        private String name;
        private String regno;
        private String email;
    }
    
    @Data
    public static class Api {
        private String baseUrl;
        private String webhookPath;
        private String submitPath;
    }
}
