package com.example.vlad.transit.api;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A bean representing the route trace (itinerary between two stops)
 * @author vshlimovich
 * @created 6/27/2022
 */
public class RouteTraceBean {
    /** Exactly what it is called */
    private StopBean initialStop;
    /** Exactly what it is called */
    private StopBean destinationStop;
    /** The map of the Route ID -> Route */
    private final Map<String, RouteStopBean> routeTraceMap = new LinkedHashMap<>();

    public StopBean getInitialStop() {
        return initialStop;
    }

    public void setInitialStop(final StopBean argInitialStop) {
        initialStop = argInitialStop;
    }

    public StopBean getDestinationStop() {
        return destinationStop;
    }

    public void setDestinationStop(final StopBean argDestinationStop) {
        destinationStop = argDestinationStop;
    }

    public Map<String, RouteStopBean> getRouteTraceMap() {
        return routeTraceMap;
    }

    public RouteTraceBean(final StopBean argInitialStop, final StopBean argDestinationStop) {
        initialStop = argInitialStop;
        destinationStop = argDestinationStop;
    }
}
