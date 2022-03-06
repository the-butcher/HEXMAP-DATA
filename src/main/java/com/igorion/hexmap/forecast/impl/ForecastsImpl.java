package com.igorion.hexmap.forecast.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.igorion.hexmap.forecast.IForecast;
import com.igorion.hexmap.forecast.IForecasts;

public class ForecastsImpl implements IForecasts {

    public static final SimpleDateFormat DATE_FORMAT_COMPARE = new SimpleDateFormat("dd.MM.yyyy");
    private Date maxDate;

    private final List<IForecast> forecasts;

    public ForecastsImpl() {
        this.forecasts = new ArrayList<>();
        this.maxDate = new Date();
    }

    public void addForecast(IForecast forecast) {
        this.forecasts.add(forecast);
        if (forecast.getDate().getTime() > this.maxDate.getTime()) {
            this.maxDate = forecast.getDate();
        }
    }

    @Override
    public Date getMaxDate() {
        return this.maxDate;
    }

    @Override
    public Optional<IForecast> optForecast(Date date, String gkz) {
        String dateRaw = DATE_FORMAT_COMPARE.format(date);
        for (IForecast forecast : this.forecasts) {
            if (DATE_FORMAT_COMPARE.format(forecast.getDate()).equals(dateRaw) && forecast.getGkz().equals(gkz)) {
                return Optional.of(forecast);
            }
        }
        return Optional.empty();
    }

}
