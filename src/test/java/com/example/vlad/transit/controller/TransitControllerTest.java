package com.example.vlad.transit.controller;

import com.example.vlad.basic.Application;
import com.example.vlad.transit.IntegrationTestConfig;
import com.example.vlad.transit.api.ResponsePayload;
import com.example.vlad.transit.api.StopBean;
import com.example.vlad.transit.connector.mbta.TransitConnectorMBTAImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The tests for TransitController
 * @author vshlimovich
 * @created 7/3/2022
 */
@SpringBootTest(classes = {IntegrationTestConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransitControllerTest extends TestBase {
    private static final String ROUTES_URI = "/transit/routes?%s";
    private static final String STOPS_URI = "/transit/stops?%s";
    private static final String FILTER_PARAMS = "filter=%s";

    @Test
    @Order(1)
    public void testTests() {
        LOG.info("******** testTests ******");
    }

    @Test
    @Order(2)
    public void testRoutes() {
        LOG.info("******** testRoutes ******");
        final String uri = String.format(ROUTES_URI, String.format(FILTER_PARAMS, "0,1"));
        final List<LinkedHashMap> list = doTypedRequest(uri);
        LOG.info("RESPONSE:\n" + list);
        Assertions.assertNotNull(list, "The returned object must not be null");
        Assertions.assertEquals(8, list.size(), "Mismatch of the list size");
    }

    @Test
    @Order(3)
    public void testStops() {
        LOG.info("******** testStops ******");
        final String uri = String.format(STOPS_URI, String.format(FILTER_PARAMS, "0,1"));
        final List<LinkedHashMap> list = doTypedRequest(uri);
        LOG.info("RESPONSE:\n" + list);
        Assertions.assertNotNull(list, "The returned object must not be null");
        Assertions.assertEquals(46, list.size(), "Mismatch of the list size");
    }
    // TODO: Put more integeration test here:
}
