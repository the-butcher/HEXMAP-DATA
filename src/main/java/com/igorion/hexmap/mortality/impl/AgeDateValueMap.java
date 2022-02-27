package com.igorion.hexmap.mortality.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.igorion.hexmap.mortality.EAgeGroup;

public class AgeDateValueMap {

    private final Map<EAgeGroup, Map<Date, Integer>> valuesByDateAndAge;

    public AgeDateValueMap() {
        this.valuesByDateAndAge = new LinkedHashMap<>();
    }

    public void addValue(EAgeGroup ageGroup, Date date, int value) {
        this.valuesByDateAndAge.computeIfAbsent(ageGroup, g -> new LinkedHashMap<>()).compute(date, (k, v) -> v != null ? v + value : value);
    }

    public double getYearlyValue(EAgeGroup ageGroup, Date date) {

        Map<Date, Integer> valuesByDate = this.valuesByDateAndAge.get(ageGroup);
        List<Date> dateList = new ArrayList<>(valuesByDate.keySet());

        double value = 0;
        for (int i = 0; i < dateList.size(); i++) {
            Date dateB = dateList.get(i);
            if (dateB.getYear() == date.getYear()) {
                value += valuesByDate.get(dateB);
            }

        }
        return value;

    }

    public double getWeeklyValue(EAgeGroup ageGroup, Date date) {

        Map<Date, Integer> valuesByDate = this.valuesByDateAndAge.get(ageGroup);
        List<Date> dateList = new ArrayList<>(valuesByDate.keySet());

        if (date.getTime() <= dateList.get(0).getTime()) { // earlier than first date
            return valuesByDate.get(dateList.get(0));
        } else if (date.getTime() >= dateList.get(dateList.size() - 1).getTime()) { // later than last date
            return valuesByDate.get(dateList.get(dateList.size() - 1));
        } else {
            for (int i = 1; i < dateList.size(); i++) {
                Date dateA = dateList.get(i - 1);
                Date dateB = dateList.get(i);
                if (date.getTime() >= dateA.getTime() && date.getTime() < dateB.getTime()) {
                    double fraction = (date.getTime() - dateA.getTime()) * 1.0 / (dateB.getTime() - dateA.getTime());
                    double population = valuesByDate.get(dateA) + (valuesByDate.get(dateB) - valuesByDate.get(dateA)) * fraction;
                    return population;
                }
            }
            throw new IllegalStateException("failed to find interpolateable dates: " + date);
        }

    }

}
