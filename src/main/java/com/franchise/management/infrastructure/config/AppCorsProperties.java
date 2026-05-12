package com.franchise.management.infrastructure.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cors")
public class AppCorsProperties {

    private List<String> allowedOriginPatterns = new ArrayList<>(List.of("*"));
    private boolean allowCredentials = false;

    public List<String> getAllowedOriginPatterns() {
        if (allowedOriginPatterns == null || allowedOriginPatterns.isEmpty()) {
            return List.of("*");
        }
        return List.copyOf(allowedOriginPatterns);
    }

    public void setAllowedOriginPatterns(List<String> allowedOriginPatterns) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }

    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }
}
