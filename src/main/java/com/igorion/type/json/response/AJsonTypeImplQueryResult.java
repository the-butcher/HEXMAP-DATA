package com.igorion.type.json.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.igorion.type.json.impl.AJsonTypeImplFeature;
import com.igorion.type.json.impl.AJsonTypeImplGeometry;

/**
 * json mapping for an arcgis server layer response<br>
 *
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AJsonTypeImplQueryResult<G extends AJsonTypeImplGeometry, F extends AJsonTypeImplFeature<G>> extends AJsonTypeImplResponse {

    @JsonProperty("objectIdFieldName")
    private String objectIdFieldName;

    @JsonProperty("fields")
    private List<JsonTypeImplField> fields = new ArrayList<>();

    @JsonProperty("features")
    private List<F> features = new ArrayList<>();

    public String getObjectIdFieldName() {
        return this.objectIdFieldName;
    }

    public List<JsonTypeImplField> getFields() {
        return this.fields;
    }

    public List<F> getFeatures() {
        return this.features;
    }

    @Override
    public String toString() {
        return String.format("%s [field-count: %s, feature-count: %s]", getClass().getSimpleName(), getFields().size(), getFeatures().size());
    }

}
