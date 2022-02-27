package com.igorion.util.impl;

import java.util.ArrayList;
import java.util.List;

public class Statistics {

    private double average;
    private double variance;
    private double standardDeviation;

    private final List<Double> values;
    private boolean isDirty;

    /**
     * create instance for the given set of values<br>
     * the values may be a complete set of rgb values from a raster and therefore shall not be kept as instance variable due to memory considerations<br>
     *
     * @param channel
     * @param values
     */
    public Statistics() {
        this.values = new ArrayList<>();
        this.isDirty = false;

    }

    public void addValue(Double value) {
        this.values.add(value);
        this.isDirty = true;
    }

    protected synchronized void recaclculate() {

        this.average = this.values.stream().mapToDouble(Double::doubleValue).average().orElseGet(() -> 0);
        this.variance = this.values.stream().mapToDouble(v -> Math.pow(v - this.average, 2)).sum() / this.values.size();
        this.standardDeviation = Math.sqrt(this.variance);

        this.isDirty = false;

    }

//  @Override
//  public double getMinimum() {
//      return this.minimum;
//  }
//
//  @Override
//  public double getMaximum() {
//      return this.maximum;
//  }

    public synchronized double getAverage() {
        if (this.isDirty) {
            recaclculate();
        }
        return this.average;
    }

    public synchronized double getVariance() {
        if (this.isDirty) {
            recaclculate();
        }
        return this.variance;
    }

    public synchronized double getStandardDeviation() {
        if (this.isDirty) {
            recaclculate();
        }
        return this.standardDeviation;
    }

    @Override
    public String toString() {
        return String.format("%s [avg: %8.6f]", getClass().getSimpleName(), this.average);
    }

}
