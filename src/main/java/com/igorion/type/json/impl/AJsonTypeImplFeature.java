package com.igorion.type.json.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IFieldType;
import com.igorion.report.value.FieldTypes;

/**
 * json mapping for an arcgis server feature<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public abstract class AJsonTypeImplFeature<G extends AJsonTypeImplGeometry> extends AJsonTypeImpl implements IDataEntry<String, Long> {

    @JsonProperty
    Map<String, Object> attributes = new HashMap<>();

    @JsonIgnore
    double m;

    public double getM() {
        return this.m;
    }

    public void setM(double m) {
        this.m = m;
    }

    @JsonAnySetter
    public void setAttribute(String mame, Object value) {
        this.attributes.put(mame, value);
    }

    @JsonProperty("geometry")
    private G geometry;

    @Override
    public int hashCode() {
        return this.attributes.hashCode() * 31;
    }

    public abstract Class<G> getGeometryType();

    @Override
    public Long getKey() {
        return optValue("OBJECTID", FieldTypes.LONG).orElseGet(() -> null);
    }

    @Override
    public <T> Optional<T> optValue(String name, IFieldType<T> type) {
        if (this.attributes.containsKey(name)) {
            return type.optValue(this.attributes.get(name));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof AJsonTypeImplFeature)) {
            return false;
        }
        AJsonTypeImplFeature<?> otherFeature = (AJsonTypeImplFeature<?>) other;
        return this.attributes.equals(otherFeature.attributes);
    }

    public void setGeometry(G geometry) {
        this.geometry = geometry;
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

    public G getGeometry() {
        return this.geometry;
    }

    @Override
    public String toString() {
        return String.format("%s [attributes: %s, geometry: %s]", getClass().getSimpleName(), this.attributes, getGeometry());
    }

}
