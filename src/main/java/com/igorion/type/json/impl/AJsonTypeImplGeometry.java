package com.igorion.type.json.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * placeholder supertype of json-geometries<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public abstract class AJsonTypeImplGeometry extends AJsonTypeImpl {

    public abstract List<List<Double>> getAllCoordinates();

    public double getXMin() {
        return getAllCoordinates().stream().mapToDouble(coordinate -> coordinate.get(0)).min().orElse(0);
    }

    public double getYMin() {
        return getAllCoordinates().stream().mapToDouble(coordinate -> coordinate.get(1)).min().orElse(0);
    }

    public double getXMax() {
        return getAllCoordinates().stream().mapToDouble(coordinate -> coordinate.get(0)).max().orElse(0);
    }

    public double getYMax() {
        return getAllCoordinates().stream().mapToDouble(coordinate -> coordinate.get(1)).max().orElse(0);
    }

    public double getWidth() {
        return getXMax() - getXMin();
    }

    public double getHeight() {
        return getYMax() - getYMin();
    }

}
