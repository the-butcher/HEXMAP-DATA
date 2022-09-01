package com.igorion.hexmap.mortality.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class LoessWrapper {

    private final String bkz;
    private PolynomialSplineFunction spline;
    private final List<Double> xVals;
    private final List<Double> yVals;
    private final double bandWidth;

    public LoessWrapper(String bkz, double bandWidth) {
        this.bkz = bkz;
        this.xVals = new ArrayList<>();
        this.yVals = new ArrayList<>();
        this.bandWidth = bandWidth;
    }

    public void addValues(double xVal, double yVal) {
        if (this.xVals.isEmpty() || xVal > this.xVals.get(this.xVals.size() - 1)) {
            this.xVals.add(xVal);
            this.yVals.add(yVal);
        } else {
//            System.err.println("non increasing: " + xVal + " is not larger than " + this.xVals.get(this.xVals.size() - 1));
        }
    }

    public double getValue(double xVal) {
        if (this.spline == null) {
            double[] xValArray = this.xVals.stream().mapToDouble(d -> d).toArray();
            double[] yValArray = this.yVals.stream().mapToDouble(d -> d).toArray();
            LoessInterpolator interpolator = new LoessInterpolator(this.bandWidth, 10);
            this.spline = interpolator.interpolate(xValArray, yValArray);
        }
        if (xVal < this.xVals.get(0)) {
            xVal = this.xVals.get(0);
        }
        if (xVal > this.xVals.get(this.xVals.size() - 1)) {
            xVal = this.xVals.get(this.xVals.size() - 1);
        }
        return this.spline.value(xVal);
    }

}
