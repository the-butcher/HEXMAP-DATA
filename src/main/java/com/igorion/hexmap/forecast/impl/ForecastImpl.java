package com.igorion.hexmap.forecast.impl;

import java.util.Date;

import com.igorion.hexmap.forecast.IForecast;

public class ForecastImpl implements IForecast {

    private final Date date;

    private final String gkz;

    private final double forecast;

    private final double ci68Upper;

    private final double ci68Lower;

    public ForecastImpl(Date date, String gkz, double forecast, double ci68Upper, double ci68Lower) {
        this.date = date;
        this.gkz = gkz;
        this.forecast = forecast;
        this.ci68Upper = ci68Upper;
        this.ci68Lower = ci68Lower;
    }

    @Override
    public Date getDate() {
        return this.date;
    }

    @Override
    public String getGkz() {
        return this.gkz;
    }

    @Override
    public double getForecast() {
        return this.forecast;
    }

    @Override
    public double getCi68Upper() {
        return this.ci68Upper;
    }

    @Override
    public double getCi68Lower() {
        return this.ci68Lower;
    }

}
