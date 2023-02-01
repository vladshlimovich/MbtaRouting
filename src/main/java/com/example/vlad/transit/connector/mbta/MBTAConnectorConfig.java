package com.example.vlad.transit.connector.mbta;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * The configuration bean covering MBTA-specific properties in application.yml
 * Like:
 *
 *   mbta-connector:
 *     base-url: "https://api-v3.mbta.com"
 *     x-api-key: "a7910298f5b444a8ae9b6683ce51c782"
 *
 * @author vshlimovich
 * @created 6/21/2022
 */
@Component
@EnableConfigurationProperties
@ConfigurationProperties("mbta-connector")
@Qualifier("MBTAConnectorConfig")
public class MBTAConnectorConfig {
    /** The base URL of the MBTA service */
    private String baseUrl;
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(final String argBaseUrl) { baseUrl = argBaseUrl; }

    /** The MBTA application key */
    private String xApiKey;
    public String getXApiKey() { return xApiKey; }
    public void setXApiKey(final String argXApiKey) { xApiKey = argXApiKey; }

    /** If set to 'true' use last-modified / If-Modified-Since headers for caching */
    private boolean useLastModifiedCache;
    public boolean isUseLastModifiedCache() { return useLastModifiedCache; }
    public void setUseLastModifiedCache(final boolean argUseLastModifiedCache) { useLastModifiedCache = argUseLastModifiedCache; }
}
