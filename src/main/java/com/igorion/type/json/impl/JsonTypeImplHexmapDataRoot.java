package com.igorion.type.json.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonTypeImplHexmapDataRoot extends AJsonTypeImpl {

    public static final DateFormat DATE_FORMAT_JSON = new SimpleDateFormat("dd.MM.yyyy");

    @JsonIgnore
    private final String fileName;

    @JsonProperty("keys")
    private Map<String, Map<String, String>> keys = new LinkedHashMap<>();

    @JsonProperty("pops")
    private Map<String, List<Double>> pops = new LinkedHashMap<>();

    @JsonProperty("data")
    private Map<String, Map<String, List<Double>>> data = new LinkedHashMap<>();

    @JsonProperty("idxs")
    private List<JsonTypeImplHexmapIndex> idxs = new ArrayList<>();

    @JsonProperty("indx")
    private int indx;

    public JsonTypeImplHexmapDataRoot(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setPopulation(String key, double... populations) {
        this.pops.put(key, new ArrayList<>());
        for (double population : populations) {
            this.pops.get(key).add(population);
        }
    }

    public void addKeyset(String name, Map<String, String> keyset) {
        this.keys.put(name, keyset);
    }

    public void setIndx(int indx) {
        this.indx = indx;
    }

    public void addIdx(String name, int minY, int maxY, boolean isHiddenOption) {
        JsonTypeImplHexmapIndex idx = new JsonTypeImplHexmapIndex();
        idx.setName(name);
        idx.setMinY(minY);
        idx.setMaxY(maxY);
        idx.setHiddenOption(isHiddenOption);
        this.idxs.add(idx);
    }

    public void clearData(Date date1) {
        String dateKey = DATE_FORMAT_JSON.format(date1);
        this.data.remove(dateKey);
    }

//    public boolean hasDateKey(String dateKey) {
//        return this.data.containsKey(dateKey);
//    }

    public boolean hasKey(String dateKey, String key) {
        return this.data.containsKey(dateKey) && this.data.get(dateKey).containsKey(key);
    }

    public void addData(Date date1, String key, int index, double value) {
        addData(DATE_FORMAT_JSON.format(date1), key, index, value);
    }

//    public void setData(Date date1, String key, int index, double value) {
//        setData(DATE_FORMAT_JSON.format(date1), key, index, value);
//    }

    public void addData(String dateKey, String key, int index, double value) {
        if (!this.data.containsKey(dateKey)) {
            this.data.put(dateKey, new LinkedHashMap<>());
        }
        if (!this.data.get(dateKey).containsKey(key)) {
            this.data.get(dateKey).put(key, new ArrayList<>());
        }
        // some data at that index already ?
        if (this.data.get(dateKey).get(key).size() > index) {
            this.data.get(dateKey).get(key).set(index, this.data.get(dateKey).get(key).get(index) + value);
        } else if (this.data.get(dateKey).get(key).size() == index) {
            this.data.get(dateKey).get(key).add(value);
        } else {
            throw new IndexOutOfBoundsException(index);
        }
    }

    public void setData(String dateKey, String key, int index, double value) {
        if (!this.data.containsKey(dateKey)) {
            this.data.put(dateKey, new LinkedHashMap<>());
        }
        if (!this.data.get(dateKey).containsKey(key)) {
            this.data.get(dateKey).put(key, new ArrayList<>());
        }
        // some data at that index already ?
        if (this.data.get(dateKey).get(key).size() > index) {
            this.data.get(dateKey).get(key).set(index, value);
        } else if (this.data.get(dateKey).get(key).size() == index) {
            this.data.get(dateKey).get(key).add(value);
        } else {
            throw new IndexOutOfBoundsException(index);
        }
    }

    @JsonIgnore
    public Set<String> getDateKeys() {
        return this.data.keySet();
    }

    @JsonIgnore
    public Set<String> getKeys(String dateKey) {
        return this.data.get(dateKey).keySet();
    }

    @JsonIgnore
    public double getValue(String dateKey, String key, int index) {
        return this.data.get(dateKey).get(key).get(index);
    }

}
