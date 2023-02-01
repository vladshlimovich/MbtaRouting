package com.example.vlad.transit.controller;

import com.example.vlad.transit.api.RouteBean;
import com.example.vlad.transit.api.RouteTraceBean;
import com.example.vlad.transit.api.StopBean;
import com.example.vlad.transit.service.TransitService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Outer-facing controller covering the "transit" functionality
 * @author vshlimovich created on 12/5/2018.
 */
@RestController
@RequestMapping("transit")
public class TransitController {
    private static final Logger LOG = LogManager.getLogger(TransitController.class);

    @Autowired
    @Qualifier("TransitServiceMBTAImpl")
    private TransitService transitService;

    @PostConstruct
    void init() {
        LOG.info("Post-construct Initializing " + this.getClass().getCanonicalName());
    }

    @GetMapping(value = "/routes", produces = "application/json")
    @ResponseBody
    public List<RouteBean> routes(@RequestParam("filter") String argFilter) {
        return this.transitService.getRoutes(argFilter);
    }

    @GetMapping(value = "/stops", produces = "application/json")
    @ResponseBody
    public List<StopBean> stops(@RequestParam("filter") String argFilter) {
        return this.transitService.getStops(argFilter);
    }

    @GetMapping(value = "/routes_with_stops", produces = "application/json")
    @ResponseBody
    public List<RouteBean> routesWithStops(@RequestParam("filter") String argFilter) {
        return this.transitService.getRoutesWithStops(argFilter);
    }

    @GetMapping(value = "/stops_with_routes", produces = "application/json")
    @ResponseBody
    public Map<String, StopBean> getStopsWithRoutes(@RequestParam("filter") String argFilter) {
        return this.transitService.getStopsWithRoutes(argFilter, false);
    }

    @GetMapping(value = "/routes_trace", produces = "application/json")
    public RouteTraceBean getRoutesTrace(
        @RequestParam("filter") String argFilter,
        @RequestParam("initial_stop") String argInitialStopName,
        @RequestParam("destination_stop") String argDestinationStopName) {
        return this.transitService.traceRoutes(argFilter, argInitialStopName, argDestinationStopName);
    }
}
