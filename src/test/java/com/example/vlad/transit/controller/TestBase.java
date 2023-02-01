package com.example.vlad.transit.controller;

import com.example.vlad.transit.IntegrationTestConfig;
import com.example.vlad.transit.connector.mbta.TransitConnectorMBTAImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The base class for all tests - holds some common methods
 * @author vshlimovich
 * @created 7/4/2022
 */
public class TestBase {
    protected final Logger LOG = LogManager.getLogger(this.getClass().getCanonicalName());

    @Autowired
    @Qualifier("integrationTestConfig")
    private IntegrationTestConfig integrationTestConfig;

    /**
     * @return the WebClient instance mostly ready to be used
     */
    protected WebClient getWebClient() {
        final long startTime = System.currentTimeMillis();
        final WebClient webClient = WebClient.builder()
            .baseUrl(this.integrationTestConfig.getBaseUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
        if (LOG.isDebugEnabled()) LOG.debug("Initialized WebClient in " + (System.currentTimeMillis() - startTime) + " ms");
        return webClient;
    }

    /**
     * Here the actual request is dispatched to the server
     * @param argUri the URI for the request
     * @return the String representing the response payload (JSON)
     */
    protected String doRawRequest(final String argUri) {
        final long startTime = System.currentTimeMillis();
        final AtomicBoolean dataIsNotModified = new AtomicBoolean(false);
        final WebClient.RequestHeadersSpec<?> headerSpec = this.getWebClient()
            .get()
            .uri(argUri);
        // TODO: Add whatever needs to be here additionally:
        final String jsonAsString = headerSpec
            .exchangeToMono(argClientResponse -> {
                if (LOG.isDebugEnabled())
                    LOG.debug(String.format("Request %s return ststus %s in %d ms", argUri, argClientResponse.statusCode(), System.currentTimeMillis() - startTime));
                // TODO: Add whatever needs to be here additionally:
                return argClientResponse.toEntity(String.class);
            })
            .block()
            .getBody()
            ;
        return jsonAsString;
    }

    /**
     * Request the server converting the response payload to the type T
     * @param argUri the request's URI
     * @param <T> the type to convert to
     * @return the instance of the specified type (or null)
     */
    protected <T> T doTypedRequest(final String argUri) {
        final String jsonAsString = doRawRequest(argUri);
        try {
            final T responseDataObject = new ObjectMapper().readValue(jsonAsString, new TypeReference<T>() {} );
            return responseDataObject;
        } catch (final JsonProcessingException argE) {
            LOG.error("Error while Unmarshalling the payload: " + jsonAsString, argE);
        }
        return null;
    }

}
