package com.example.vlad.transit.api;

import java.util.Map;

/**
 * Bean representing a single element received from the underlying service
 * @author vshlimovich
 * @created 6/24/2022
 */
public class ElementBean {
    protected Map<String, Object> attributes;
    protected String id;
    protected String type;

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(final Map<String, Object> argAttributes) {
        attributes = argAttributes;
    }

    public String getId() {
        return id;
    }

    public void setId(final String argId) {
        id = argId;
    }

    public String getType() {
        return type;
    }

    public void setType(final String argType) {
        type = argType;
    }

    public ElementBean() {
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ElementBean{");
        sb.append("attributes=").append(attributes);
        sb.append(", id='").append(id).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
