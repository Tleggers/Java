package com.Trekkit_Java.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// 2025-06-05 완

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;

    // Getter, Setter
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}