package com.igorion.hexmap.mortality;

import java.util.Date;

public interface IMortality {

    static final Date REFERENCE_DATE = new Date(1640995200000L); // Date and time (GMT): Saturday, January 1, 2022 0:00:00

    void addDeaths(EAgeGroup ageGroup, Date date, double deaths);

    double getWeeklyDeaths(EAgeGroup ageGroup, Date date);

    double getYearlyDeaths(EAgeGroup ageGroup, Date date);

    default double getReferenceMortality(INutsRegion region, Date normalizeDate) {

        double totalReferencePopulation = 0;
        double totalReferenceDeaths = 0;

        for (EAgeGroup ageGroup : EAgeGroup.values()) {

            if (ageGroup == EAgeGroup.ETOTAL) {
                continue;
            }

            double referencePopulation = region.getPopulation(ageGroup, REFERENCE_DATE);
            double referenceDeaths = getWeeklyDeaths(ageGroup, normalizeDate);

            totalReferencePopulation += referencePopulation;
            totalReferenceDeaths += referenceDeaths;

        }

        return totalReferenceDeaths * 100000 / totalReferencePopulation;

    }

    default double getNormalizedMortality(INutsRegion region, Date normalizeDate) {

        double totalReferencePopulation = 0;
        double totalReferenceDeaths = 0;

        for (EAgeGroup ageGroup : EAgeGroup.values()) {

            if (ageGroup == EAgeGroup.ETOTAL) {
                continue;
            }

            // get population at reference date and normalizeable date
            double referencePopulation = region.getPopulation(ageGroup, REFERENCE_DATE);
            double normalizePopulation = region.getPopulation(ageGroup, normalizeDate);

            // get actual deaths at normalizeable date
            double normalizeDeaths = getWeeklyDeaths(ageGroup, normalizeDate);
            double referenceDeaths = normalizeDeaths * (referencePopulation / normalizePopulation);

            totalReferencePopulation += referencePopulation;
            totalReferenceDeaths += referenceDeaths;

        }

        return totalReferenceDeaths * 100000 / totalReferencePopulation;

    }

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
