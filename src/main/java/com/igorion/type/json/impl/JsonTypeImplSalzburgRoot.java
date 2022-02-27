package com.igorion.type.json.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonTypeImplSalzburgRoot extends AJsonTypeImpl {

    @JsonProperty("data")
    private Map<String, Map<String, JsonTypeImplSalzburgData>> data = new LinkedHashMap<>();

    public Map<String, Map<String, JsonTypeImplSalzburgData>> getData() {
        return this.data;
    }
}
