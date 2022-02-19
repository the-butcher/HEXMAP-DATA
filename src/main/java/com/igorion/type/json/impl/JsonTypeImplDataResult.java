package com.igorion.type.json.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * json mapping for an portal add item (upload) response<br>
 *
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonTypeImplDataResult extends AJsonTypeImpl {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("src")
    private String src;

    @JsonProperty("dst")
    private String dst;

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    @Override
    public String toString() {
        return String.format("%s [success: %s, src: %s]", getClass().getSimpleName(), this.src, this.dst);
    }

}
