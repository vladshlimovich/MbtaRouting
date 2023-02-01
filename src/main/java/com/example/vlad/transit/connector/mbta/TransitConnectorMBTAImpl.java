package com.example.vlad.transit.connector.mbta;

import com.example.vlad.transit.api.ResponsePayload;
import com.example.vlad.transit.api.RouteBean;
import com.example.vlad.transit.api.StopBean;
import com.example.vlad.transit.connector.TransitConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MBTA-specific implementation of transit connect
 * @author vshlimovich
 * @created 6/21/2022
 */
@Component
@Qualifier("TransitConnectorMBTAImpl")
public class TransitConnectorMBTAImpl implements TransitConnector {
    private static final Logger LOG = LogManager.getLogger(TransitConnectorMBTAImpl.class);
    /** The nam of the header use to pass the api key*/
    private static final String API_KEY_HEADER = "x-api-key";
    /** The format pattern for routes portion of the URI*/
    private static final String ROUTES_URI = "/routes?%s";
    /** The format pattern for sops portion of the URI*/
    private static final String STOPS_URI = "/stops?%s";
    /** The name of the cache-related response header */
    private static final String LAST_MODIFIED_HEADER_NAME = "last-modified";
    /** The name of the cache-related request header */
    private static final String IF_MODIFIED_SINCE_HEADER_NAME = "If-Modified-Since";
    /** The "global" map storing the LAST_MODIFIED_HEADER_NAME header value by uri */
    private final Map<String, String> lastModifiedMap = new HashMap<>();
    /** The "global" map storing the Last Modified value of the returned payload by uri */
    private final Map<String, String> payloadByUriMap = new HashMap<>();

    @Autowired
    @Qualifier("MBTAConnectorConfig")
    private MBTAConnectorConfig mbtaConfig;

    @PostConstruct
    void init() {
        LOG.info("Post-construct Initializing " + this.getClass().getCanonicalName());
    }

    private WebClient getWebClient() {
        final long startTime = System.currentTimeMillis();
        final WebClient webClient = WebClient.builder()
            .baseUrl(this.mbtaConfig.getBaseUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(API_KEY_HEADER,this.mbtaConfig.getXApiKey())
            .build();
        if (LOG.isDebugEnabled()) LOG.debug("Initialized WebClient in " + (System.currentTimeMillis() - startTime) + " ms");
        return webClient;
    }


    @Override
    public List<RouteBean> getRoutes(final String argParams) {
        final String uri = String.format(ROUTES_URI, argParams);
        final String jsonAsString = doRawMBTARequest(uri);
        try {
            final ResponsePayload<RouteBean> responsePayload = new ObjectMapper().readValue(jsonAsString, new TypeReference<ResponsePayload<RouteBean>>() {} );
            if (responsePayload != null) {
                return responsePayload.getData();
            } else {
                LOG.error("Got a null payload");
            }
        } catch (final JsonProcessingException argE) {
            LOG.error("Error while Unmarshalling the payload: " + jsonAsString, argE);
        }
        return null;
    }

    @Override
    public List<StopBean> getStops(final String argParams) {
        final String uri = String.format(STOPS_URI, argParams);

        final String jsonAsString = doRawMBTARequest(uri);
        try {
            final ResponsePayload<StopBean> responsePayload = new ObjectMapper().readValue(jsonAsString, new TypeReference<ResponsePayload<StopBean>>() {} );
            if (responsePayload != null) {
                return responsePayload.getData();
            } else {
                LOG.error("Got a null payload");
            }
        } catch (final JsonProcessingException argE) {
            LOG.error("Error while Unmarshalling the payload: " + jsonAsString, argE);
        }
        return null;
    }

    /**
     * Here the actual request is dispatched to mbta.com
     * @param uri the URI for the request
     * @return the String representing the response payload (JSON)
     */
    private String doRawMBTARequest(final String uri) {
        final long startTime = System.currentTimeMillis();
        final AtomicBoolean dataIsNotModified = new AtomicBoolean(false);
        final WebClient.RequestHeadersSpec<?> headerSpec = this.getWebClient()
            .get()
            .uri(uri);

        final String lastModifiedMarker = this.lastModifiedMap.get(uri);
        if (this.mbtaConfig.isUseLastModifiedCache() && StringUtils.hasLength(lastModifiedMarker)) {
            headerSpec.header(IF_MODIFIED_SINCE_HEADER_NAME, lastModifiedMarker);
        }

        String jsonAsString = headerSpec
            .exchangeToMono(argClientResponse -> {
                dataIsNotModified.set(argClientResponse.statusCode().equals(HttpStatus.NOT_MODIFIED));
                if (LOG.isDebugEnabled())
                    LOG.debug(String.format("Request %s return ststus %s in %d ms", uri, argClientResponse.statusCode(), System.currentTimeMillis() - startTime));
                // Cache the 'last-modified' response header value by the uri:
                final List<String> headerList = argClientResponse.headers().header(LAST_MODIFIED_HEADER_NAME);
                if (headerList != null && headerList.size() > 0 && headerList.get(0).trim().length() > 0) {
                    this.lastModifiedMap.put(uri, headerList.get(0));
                }
                return argClientResponse.toEntity(String.class);
            })
            .block()
            .getBody()
        ;

        if (dataIsNotModified.get()) {
            jsonAsString = this.payloadByUriMap.get(uri);
            if (!StringUtils.hasLength(jsonAsString)) {
                LOG.error("Gor blank response for: " + uri);
            }
        }
        this.payloadByUriMap.put(uri, jsonAsString);
        return jsonAsString;
    }
}
