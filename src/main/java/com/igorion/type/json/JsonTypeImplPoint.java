package com.igorion.type.json;

import java.util.Arrays;
import java.util.Collections;
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
 * @since 14.03.2020
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class JsonTypeImplPoint extends AJsonTypeImplGeometry {

    @JsonProperty("x")
    double x;

    @JsonProperty("y")
    double y;

    @Override
    public List<List<Double>> getAllCoordinates() {
        return Collections.singletonList(Arrays.asList(this.x, this.y));
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("%s [x: %s, y: %s]", getClass().getSimpleName(), getX(), getY());
    }

}
