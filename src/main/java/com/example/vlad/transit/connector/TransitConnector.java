package com.example.vlad.transit.connector;

import com.example.vlad.transit.api.RouteBean;
import com.example.vlad.transit.api.StopBean;

import java.util.List;

/**
 * Interface describing the methods to connect with the underlying transit service
 * @author vshlimovich
 * @created 6/25/2022
 */
public interface TransitConnector {
    /**
     * Gets the routes
     * @param argParams the prarameters to the call
     * @return the List<RouteBean> retrieved according to passed argParams
     */
    List<RouteBean> getRoutes(final String argParams);
    /**
     * Gets the stops
     * @param argParams the prarameters to the call
     * @return the List<StopBean> retrieved according to passed argParams
     */
    List<StopBean> getStops(final String argParams);
}
