package com.igorion.type.json.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonTypeImplMortalityDataRoot extends AJsonTypeImpl {

    public static final DateFormat DATE_FORMAT_JSON = new SimpleDateFormat("yyyy-MM-dd");

    @JsonProperty("name")
    private String name;

    @JsonProperty("minYear")
    private int minYear;

    @JsonProperty("maxYear")
    private int maxYear;

    @JsonProperty("data")
    private Map<String, JsonTypeImplMortalityDataItem> data = new LinkedHashMap<>();

    public void setName(String name) {
        this.name = name;
    }

    public void setMinYear(int minYear) {
        this.minYear = minYear;
    }

    public void setMaxYear(int maxYear) {
        this.maxYear = maxYear;
    }

    public void addItem(Date date, JsonTypeImplMortalityDataItem dataItem) {
        this.data.put(DATE_FORMAT_JSON.format(date), dataItem);
    }

}
