package com.igorion.hexmap.mortality;

import java.util.Date;

public interface IMortality {

    void addDeaths(EAgeGroup ageGroup, Date date, int deaths);

    double getWeeklyDeaths(EAgeGroup ageGroup, Date date);

    double getYearlyDeaths(EAgeGroup ageGroup, Date date);

    /**
     * get the mortality for this {@link INutsRegion}, {@link EAgeGroup} and {@link Date} (the date's month and day are relevant while the year will be ignored
     * @param region
     * @param ageGroup
     * @param date
     * @return
     */
    default double getWeeklyMortality(INutsRegion region, EAgeGroup ageGroup, Date date) {

        // Date mappedDate = new Date(getYear() - 1900, date.getMonth(), date.getDate());
        // System.out.println("mapped date: " + date + " to: " + mappedDate);

        double population = region.getPopulation(ageGroup, date);
        double weeklyDeaths = getWeeklyDeaths(ageGroup, date);
        return weeklyDeaths * 100000 / population;

    }

    default double getYearlyMortality(INutsRegion region, EAgeGroup ageGroup, Date date) {

        // Date mappedDate = new Date(getYear() - 1900, date.getMonth(), date.getDate());
        // System.out.println("mapped date: " + date + " to: " + mappedDate);

        double population = region.getPopulation(ageGroup, date);
        double yearlyDeaths = getYearlyDeaths(ageGroup, date);
        return yearlyDeaths * 100000 / population;

    }

}
