package com.example.vlad.basic.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author vshlimovich created on 12/5/2018.
 */
@Component
@EnableConfigurationProperties
@ConfigurationProperties("identity")
public class IdentityBean {
    private String appname;
    private String author;

    public String getAppname() {
        return appname;
    }
    public void setAppname(final String argAppname) {
        appname = argAppname;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(final String argAuthor) {
        author = argAuthor;
    }
}
