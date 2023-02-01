package com.example.vlad.transit.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean representing underlying service response
 * @author vshlimovich
 * @created 6/24/2022
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsePayload<T> {
    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(final List<T> argData) {
        data = argData;
    }

    public ResponsePayload() {
    }
}
