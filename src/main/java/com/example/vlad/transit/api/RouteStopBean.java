package com.example.vlad.transit.api;

/**
 * Bean representing Route / Stop pair
 * @author vshlimovich
 * @created 6/29/2022
 */
public class RouteStopBean {
    private RouteBean route;
    private StopBean stop;

    public RouteBean getRoute() {
        return route;
    }

    public void setRoute(final RouteBean argRoute) {
        route = argRoute;
    }

    public StopBean getStop() {
        return stop;
    }

    public void setStop(final StopBean argStop) {
        stop = argStop;
    }

    public RouteStopBean(final RouteBean argRoute, final StopBean argStop) {
        route = argRoute;
        stop = argStop;
    }

    public RouteStopBean() {
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(route);
        sb.append(" / ").append(stop);
        return sb.toString();
    }
}
