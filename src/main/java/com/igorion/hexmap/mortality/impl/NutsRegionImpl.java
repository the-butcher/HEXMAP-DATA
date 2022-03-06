package com.igorion.hexmap.mortality.impl;

import java.util.Date;

import com.igorion.hexmap.mortality.EAgeGroup;
import com.igorion.hexmap.mortality.INutsRegion;

public class NutsRegionImpl implements INutsRegion {

    private final String nuts;
    private final ValueDateMap populations;

    public NutsRegionImpl(String nuts) {
        this.nuts = nuts;
        this.populations = new ValueDateMap();
    }

    @Override
    public String getNuts() {
        return this.nuts;
    }

    @Override
    public void addPopulation(EAgeGroup ageGroup, Date date, int population) {
        this.populations.addValue(ageGroup, date, population);
    }

    @Override
    public double getPopulation(EAgeGroup ageGroup, Date date) {
        return this.populations.getWeeklyValue(ageGroup, date);
    }

}
