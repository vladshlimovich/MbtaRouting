package com.example.vlad.transit.service.mbta;

import com.example.vlad.transit.api.RouteBean;
import com.example.vlad.transit.api.RouteStopBean;
import com.example.vlad.transit.api.RouteTraceBean;
import com.example.vlad.transit.api.StopBean;
import com.example.vlad.transit.connector.TransitConnector;
import com.example.vlad.transit.service.TransitService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

/**
 * MBTA-specific implementation of transit service, most of the business logic is concentrated right here
 * @author vshlimovich
 * @created 6/26/2022
 */
@Component
@Qualifier("TransitServiceMBTAImpl")
public class TransitServiceMBTAImpl implements TransitService {
    private static final Logger LOG = LogManager.getLogger(TransitServiceMBTAImpl.class);

    /** The template for the "filter" query string parameter >*/
    private static final String FILTER_PARAMS = "filter[%s]=%s";
    /** The "type"  filter >*/
    private static final String FILTER_TYPE = "type";
    /** The "route"  filter >*/
    private static final String FILTER_ROUTE = "route";

    /** The "name"  attribute >*/
    private static final String ATTRIBUTE_NAME = "name";

    @Autowired
    @Qualifier("TransitConnectorMBTAImpl")
    private TransitConnector transitConnector;

    @PostConstruct
    void init() {
        LOG.info("Post-construct Initializing " + this.getClass().getCanonicalName());
    }

    @Override
    public List<RouteBean> getRoutes(final String argParams) {
        final List<RouteBean> beanList = this.transitConnector.getRoutes(String.format(FILTER_PARAMS, FILTER_TYPE, argParams));
        return beanList;
    }

    @Override
    public List<StopBean> getStops(final String argParams) {
        final List<StopBean> beanList = this.transitConnector.getStops(String.format(FILTER_PARAMS, FILTER_ROUTE, argParams));
        return beanList;
    }

    @Override
    public List<RouteBean> getRoutesWithStops(final String argParams) {
        final List<RouteBean> routes = getRoutes(argParams);
        if (routes != null) {
            routes.forEach(argRouteBean -> {
                final String routeId = argRouteBean.getId();
                final List<StopBean> stops = getStops(routeId);
                if (stops != null) {
                    stops.forEach(argStopBean -> argRouteBean.getStopBeanMap().put(argStopBean.getId(), argStopBean));
                }
            });
        } else {
            LOG.error("routes == null for argParams: " + argParams);
        }
        sortRoutesByNumberOfStops(routes, false);
        return routes;
    }

    @Override
    public Map<String, StopBean> getStopsWithRoutes(final String argParams, final boolean argPopulateFully) {
        final List<RouteBean> routes = getRoutesWithStops(argParams);
        final Map<String, StopBean> stopsWithRoutes = new LinkedHashMap<>();
        final List<StopBean> sortedStops = new ArrayList<>();
        if (routes != null) {
            routes.forEach(argRouteBean -> {
                final String routeId = argRouteBean.getId();
                final List<StopBean> stops = getStops(routeId);
                sortStopsByNumberOfRoutes(stops, false);
                if (stops != null) {
                    stops.forEach(argStopBean -> {
                        StopBean stopBean = stopsWithRoutes.get(argStopBean.getId());
                        if (stopBean == null) {
                            stopBean = argStopBean;
                            stopsWithRoutes.put(stopBean.getId(), stopBean);

                            sortedStops.add(stopBean);
                        }
                        stopBean.getRouteBeanMap().put(argRouteBean.getId(), argRouteBean);
                        if (argPopulateFully) {
                            if (!"Red".equalsIgnoreCase(stopBean.getId())) {
                                argRouteBean.getStopBeanMap().put(stopBean.getId(), stopBean);
                            }
                        }
                    });
                }
            });
        } else {
            LOG.error("routes == null for argParams: " + argParams);
        }
        sortStopsByNumberOfRoutes(sortedStops, false);
        stopsWithRoutes.clear();
        sortedStops.forEach(argStopBean -> stopsWithRoutes.put(argStopBean.getId(), argStopBean));
        return stopsWithRoutes;
    }
    private static final StopBean ERROR_BEAN = new StopBean();
    static {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(ATTRIBUTE_NAME, "ERROR: Nonexisting");
        ERROR_BEAN.setAttributes(attributes);
    }

    @Override
    public RouteTraceBean traceRoutes(final String argParams, final String argInitialStopName, final String argDestinationStopName) {
        final Map<String, StopBean> stopsWithRoutes = getStopsWithRoutes(argParams, true);
        StopBean initalStopBean = null;
        StopBean destinationStopBean = null;
        try {
            initalStopBean = stopsWithRoutes
                .values()
                .stream()
                .filter(argBean -> argInitialStopName.trim().equalsIgnoreCase("" + argBean.getAttributes().get(ATTRIBUTE_NAME))).findFirst().get();
        } catch (Exception argE) {
            initalStopBean = ERROR_BEAN;
        }
        try {
            destinationStopBean = stopsWithRoutes
                .values()
                .stream()
                .filter(argBean -> argDestinationStopName.trim().equalsIgnoreCase("" + argBean.getAttributes().get(ATTRIBUTE_NAME))).findFirst().get();
        } catch (Exception argE) {
            destinationStopBean = ERROR_BEAN;
        }

        final RouteTraceBean routeTraceBean = new RouteTraceBean(initalStopBean, destinationStopBean);
        final List<RouteStopBean> foundRouts = findRoutes(initalStopBean, destinationStopBean);
        routeTraceBean.getInitialStop().getRouteBeanMap().clear();
        routeTraceBean.getDestinationStop().getRouteBeanMap().clear();
        destinationStopBean.getRouteBeanMap().clear();
        foundRouts.forEach(argRouteStopBean -> {
            // Lighten the bean:
            final RouteBean routeBean = argRouteStopBean.getRoute();
            routeBean.getStopBeanMap().clear();
            final StopBean stopBean = argRouteStopBean.getStop();
            stopBean.getRouteBeanMap().clear();
            routeTraceBean.getRouteTraceMap().put(routeBean.getId(), new RouteStopBean(routeBean, stopBean));
        });
        return routeTraceBean;
    }

    /**
     * Sortes the passed argRoutes by the number of stops
     * @param argRoutes the list to sort
     * @param argAsc sorts ASCENDING if 'true', DESCENDING otherwise
     */
    private void sortRoutesByNumberOfStops(final List<RouteBean> argRoutes, final boolean argAsc) {
        Collections.sort(argRoutes, (o1, o2) -> {
            if (o1.getStopBeanMap() != null && o1.getStopBeanMap() != null) {
                return argAsc
                    ? o1.getStopBeanMap().size() - o2.getStopBeanMap().size()
                    : o2.getStopBeanMap().size() - o1.getStopBeanMap().size();
            } else if (o1.getStopBeanMap() != null) {
                return 1;
            } else if (o2.getStopBeanMap() != null) {
                return -1;
            }
            return 0;
        });
    }

    /**
     * Sortes the passed argStops by the number of routes
     * @param argStops the list to sort
     * @param argAsc sorts ASCENDING if 'true', DESCENDING otherwise
     */
    private void sortStopsByNumberOfRoutes(final List<StopBean> argStops, final boolean argAsc) {
        Collections.sort(argStops, (o1, o2) -> {
            if (o1.getRouteBeanMap() != null && o1.getRouteBeanMap() != null) {
                return argAsc
                    ? o1.getRouteBeanMap().size() - o2.getRouteBeanMap().size()
                    : o2.getRouteBeanMap().size() - o1.getRouteBeanMap().size();
            } else if (o1.getRouteBeanMap() != null) {
                return 1;
            } else if (o2.getRouteBeanMap() != null) {
                return -1;
            }
            return 0;
        });
    }

    /**
     * Finds the route trace (itinerary) between the two stops
     * @param argInitialStop the starting point of the trip
     * @param argDestinationStop the ending point of the trip
     * @return the List<RouteBean> - the list of the routes in the order of the trip
     */
    public List<RouteStopBean> findRoutes(final StopBean argInitialStop, final StopBean argDestinationStop) {
        final long startTime = System.currentTimeMillis();
        final Stack<RouteStopBean> stack = new Stack<>();
        if (argInitialStop != ERROR_BEAN && argDestinationStop != ERROR_BEAN) {
            final Set<String> visitedRoutes = new HashSet<>();
//            visitedRoutes.add("Red");
            final Set<String> visitedStops = new HashSet<>();
            boolean found = checkRout(argInitialStop, argDestinationStop, stack, visitedRoutes, visitedStops);
            LOG.info("rout " + (found ? "found" : "not found"));
        }
        if (LOG.isDebugEnabled()) LOG.debug(String.format("findRoutes took %d ms", System.currentTimeMillis() - startTime));
        return stack;
    }

    /**
     * Checks if the argDestinationStop is reachable from the argCurrentStop
     * ATTENTION! the method is called RECURCIVELY.  This is more or less a Graph BFS implementation
     * @param argCurrentStop the starting point of the trip
     * @param argDestinationStop the ending point of the trip
     * @param argStack the stack of the routes to complete the trip
     * @param argVisitedRoutes the Set<String> of the visited route IDs
     * @param argVisitedStops the Set<String> of the visited stop IDs
     * @return 'true' if the argDestinationStop is reachable from the argCurrentStop, 'false' otherwise
     */
    private boolean checkRout(
        final StopBean argCurrentStop,
        final StopBean argDestinationStop,
        final Stack<RouteStopBean> argStack,
        final Set<String> argVisitedRoutes,
        final Set<String> argVisitedStops) {
        boolean result = false;
        if (LOG.isDebugEnabled()) LOG.debug("STOP: " + argCurrentStop);
        argVisitedStops.add(argCurrentStop.getId());
        // If argCurrentStop ended up being on the same route as argDestinationStop - FOUND!!!
        if (argCurrentStop != null && argDestinationStop != null) {
            final Set<String> commonRouteIDs = new HashSet<>(argDestinationStop.getRouteBeanMap().keySet());
            commonRouteIDs.retainAll(argCurrentStop.getRouteBeanMap().keySet());
            if (commonRouteIDs.size() > 0) {
                final String routeId = commonRouteIDs.stream().findFirst().get();
                final RouteBean routeBean = argDestinationStop.getRouteBeanMap().get(routeId);
                if (routeBean != null) {
                    argStack.push(new RouteStopBean(routeBean, argCurrentStop));
                }
                return true;
            }
        }
        // Establish the visit:
        RouteStopBean currentRouteStop = null;
        RouteBean currentRoute = null;
        if (argStack.size() > 0) {
            // Not the very first step - there is something in the stack:
            currentRouteStop = argStack.peek();
            currentRoute = currentRouteStop.getRoute();
        } else {
            // Nothing in the stack (very first iteration) - choose any of routes of the current stop:
            currentRoute = getNextRoute(argCurrentStop, argVisitedRoutes);
            argStack.push(new RouteStopBean(currentRoute, argCurrentStop));
        }
        argVisitedRoutes.add(currentRoute.getId());
        if (LOG.isDebugEnabled()) LOG.debug("ROUTE-STOP: " + new RouteStopBean(currentRoute, argCurrentStop));
        boolean needToPop = false;
        // Trying the next connection on this route (Breadth):
        final StopBean nextConnection = getNextConnection(currentRoute, argVisitedStops);
        if (nextConnection != null) {
            result = checkRout(nextConnection, argDestinationStop, argStack, argVisitedRoutes, argVisitedStops);
            if (result) {
                return result;
            }
        } else {
            // Trying the next route on this connection (Depth):
            final RouteBean nextRoute = getNextRoute(argCurrentStop, argVisitedRoutes);
            if (nextRoute != null) {
                argStack.push(new RouteStopBean(nextRoute, argCurrentStop));
                result = checkRout(argCurrentStop, argDestinationStop, argStack, argVisitedRoutes, argVisitedStops);
                if (!result) {
                    if (argStack.size() > 1) {
                        final RouteStopBean dismissed = argStack.pop();
                        if (LOG.isDebugEnabled()) LOG.debug("^^^: " + dismissed + " visited stops: " + argVisitedStops);
                    }
                }
                return result;
            }
            return false;
        }
        return result;
    }

    /**
     * @param argRoute the route to examine
     * @param argVisitedStops the Set<String> of the visited stop IDs
     * @return the next available (not-visited) connection (stop with multiple routes) on the passed argRoute
     */
    private StopBean getNextConnection(final RouteBean argRoute, final Set<String> argVisitedStops) {
        final Set<String> unvisited = new LinkedHashSet<>(argRoute.getStopBeanMap().keySet());
        unvisited.removeAll(argVisitedStops);
        if (unvisited.size() > 0) {
            final Optional<String> opt = unvisited
                .stream()
                .filter(argId -> argRoute.getStopBeanMap().get(argId).getRouteBeanMap().size() > 1)
                .findFirst()
            ;
            return opt.isPresent() ? argRoute.getStopBeanMap().get(opt.get()) : null;
        }
        return null;
    }

    /**
     * @param argStop the stop to examine
     * @param argVisitedRoutes the Set<String> of the visited route IDs
     * @return the next available (not-visited) route  on the passed connecdtion (stop with multiple routes)
     */
    private RouteBean getNextRoute(final StopBean argStop, final Set<String>  argVisitedRoutes) {
        final Set<String> unvisited = new HashSet<>(argStop.getRouteBeanMap().keySet());
        unvisited.removeAll(argVisitedRoutes);
        if (unvisited.size() > 0) {
            final Optional<String> opt = unvisited.stream().findFirst();
            return opt.isPresent() ? argStop.getRouteBeanMap().get(opt.get()) : null;
        }
        return null;
    }
}
