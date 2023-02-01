package com.example.vlad.transit.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The bean representing a single STOP
 * @author vshlimovich
 * @created 6/24/2022
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StopBean extends ElementBean{
    private final Map<String, RouteBean> routeBeanMap = new LinkedHashMap<>();

    public Map<String, RouteBean> getRouteBeanMap() {
        return routeBeanMap;
    }

    public StopBean() {
        super();
    }

    @Override
    public String toString() {
        return "" + this.getAttributes().get("name");
    }

}
