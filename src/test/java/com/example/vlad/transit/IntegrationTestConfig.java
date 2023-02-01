package com.example.vlad.transit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author vshlimovich
 * @created 7/4/2022
 */
@Configuration
@Qualifier("integrationTestConfig")
public class IntegrationTestConfig {
    private String baseUrl = "http://localhost:1984/";
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(final String argBaseUrl) { baseUrl = argBaseUrl; }

    public IntegrationTestConfig() {
    }
}
