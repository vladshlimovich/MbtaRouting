package com.example.vlad.basic.controller;

import com.example.vlad.basic.api.IdentityBean;
import com.example.vlad.transit.controller.TransitController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * @author vshlimovich created on 12/5/2018.
 */
@RestController
@RequestMapping("actuator")
public class HealthController {
    private static final Logger LOG = LogManager.getLogger(HealthController.class);

    @Autowired
    private IdentityBean identityBean;

    @PostConstruct
    void init() {
        LOG.info("Post-construct Initializing " + this.getClass().getCanonicalName());
    }


    @GetMapping(value = "/id", produces = "application/json")
    @ResponseBody
    public IdentityBean id() {
        return this.identityBean;
    }
}
