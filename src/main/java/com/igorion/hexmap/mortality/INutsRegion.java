package com.igorion.hexmap.mortality;

import java.util.Date;

public interface INutsRegion {

    String getNuts();

    void addPopulation(EAgeGroup ageGroup, Date date, int population);

    /**
     * get the population for the given {@link IAgeGroup} at the given {@link Date}
     * @param ageGroup
     * @param date
     * @return a double value due to interpolation
     */
    double getPopulation(EAgeGroup ageGroup, Date date);

}
