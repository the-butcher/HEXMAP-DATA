package com.igorion.type.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.igorion.type.json.impl.AJsonTypeImplGeometry;

/**
 * json mapping for an arcgis server feature attributes object<br>
 *
 * @author h.fleischer
 * @since 20.06.2020
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class JsonTypeImplPolygon extends AJsonTypeImplGeometry {

    @JsonProperty("rings")
    List<List<List<Double>>> rings = new ArrayList<>();

    public void setRings(List<List<List<Double>>> rings) {
        this.rings = rings;
    }

    public List<List<List<Double>>> getRings() {
        return this.rings;
    }

    @Override
    public List<List<Double>> getAllCoordinates() {
        List<List<Double>> allCoordinates = new ArrayList<>();
        getRings().forEach(allCoordinates::addAll);
        return allCoordinates;
    }

    @Override
    public String toString() {
        return String.format("%s [rings: %s]", getClass().getSimpleName(), this.rings.size());
    }

}
