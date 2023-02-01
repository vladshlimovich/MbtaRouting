package com.example.vlad.transit.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The bean representing a single ROUTE
 * @author vshlimovich
 * @created 6/24/2022
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteBean extends ElementBean {
    private final Map<String, StopBean> stopBeanMap = new LinkedHashMap<>();

    public Map<String, StopBean> getStopBeanMap() {
        return stopBeanMap;
    }

    public RouteBean() {
        super();
    }

    @Override
    public String toString() {
        return "" + this.getAttributes().get("long_name");
    }
}
