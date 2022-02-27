package com.igorion.hexmap.mortality.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.igorion.hexmap.mortality.EAgeGroup;
import com.igorion.hexmap.mortality.INuts3Region;

public class Nuts3RegionImpl implements INuts3Region {

    private final String nuts3;
    private final AgeDateValueMap populations;
    private final Set<String> mappedGkzs;

    public Nuts3RegionImpl(String nuts3) {
        this.nuts3 = nuts3;
        this.populations = new AgeDateValueMap();
        this.mappedGkzs = new HashSet<>();
    }

    @Override
    public String getNuts3() {
        return this.nuts3;
    }

    @Override
    public void mapGkz(String gkz) {
        this.mappedGkzs.add(gkz);
    }

    @Override
    public boolean hasGkz(String gkz) {
        return this.mappedGkzs.contains(gkz);
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
