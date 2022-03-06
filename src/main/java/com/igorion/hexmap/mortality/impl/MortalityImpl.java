package com.igorion.hexmap.mortality.impl;

import java.util.Date;

import com.igorion.hexmap.mortality.EAgeGroup;
import com.igorion.hexmap.mortality.IMortality;

public class MortalityImpl implements IMortality {

    private final ValueDateMap deaths;

    public MortalityImpl() {
        this.deaths = new ValueDateMap();
    }

    @Override
    public double getYearlyDeaths(EAgeGroup ageGroup, Date date) {
        return this.deaths.getYearlyValue(ageGroup, date);
    }

    @Override
    public double getWeeklyDeaths(EAgeGroup ageGroup, Date date) {
        return this.deaths.getWeeklyValue(ageGroup, date);
    }

    @Override
    public void addDeaths(EAgeGroup ageGroup, Date date, int deaths1) {
        this.deaths.addValue(ageGroup, date, deaths1);

    }

}
