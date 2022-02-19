package com.igorion.type.json.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonTypeImplHexmapIndex extends AJsonTypeImpl {

    @JsonProperty("name")
    private String name;

    @JsonProperty("minY")
    private int minY;

    @JsonProperty("maxY")
    private int maxY;

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public void setName(String name) {
        this.name = name;
    }

}
