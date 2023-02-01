package com.example.vlad.transit.service;

import com.example.vlad.transit.api.RouteBean;
import com.example.vlad.transit.api.RouteTraceBean;
import com.example.vlad.transit.api.StopBean;

import java.util.List;
import java.util.Map;

/**
 * Interface describing the methods supporting 'transit' functionality
 * @author vshlimovich
 * @created 6/26/2022
 */
public interface TransitService {
    /**
     * Gets the routes
     * @param argParams the parameters to the call (filter)
     * @return the List<RouteBean> retrieved according to passed argParams
     */
    List<RouteBean> getRoutes(final String argParams);

    /**
     * Gets the stops
     * @param argParams the parameters to the call (filter)
     * @return the List<StopBean> retrieved according to passed argParams
     */
    List<StopBean> getStops(final String argParams);

    /**
     * Gets routes with the stops
     * @param argParams the parameters to the call (filter)
     * @return the List<RouteBean> retrieved according to passed argParams
     */
    List<RouteBean> getRoutesWithStops(final String argParams);

    /**
     * Gets stops with the routes
     * @param argParams the parameters to the call (filter)
     * @param argPopulateFully 'true' for populating fully, 'false' otherwise
     * @return the Map<String, Set<String>> retrieved according to passed argParams
     */
    Map<String, StopBean> getStopsWithRoutes(final String argParams, final boolean argPopulateFully);

    /**
     * Gets the route trace between the two stops (filter)
     * @param argParams the parameters to the call
     * @param argInitialStopName the initial stop name
     * @param argDestinationStopName the destination stop name
     * @return the RouteTraceBean representing the route trace between the two stops
     */
    RouteTraceBean traceRoutes(final String argParams, final String argInitialStopName, final String argDestinationStopName);
}
