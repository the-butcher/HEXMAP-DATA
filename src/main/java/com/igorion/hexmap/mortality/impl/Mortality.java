package com.igorion.hexmap.mortality.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.igorion.hexmap.mortality.EAgeGroup;
import com.igorion.hexmap.mortality.IMortality;
import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IDataSet;
import com.igorion.report.dataset.IDataSetFactory;
import com.igorion.report.dataset.impl.DataSetFactoryImplCsv;
import com.igorion.report.value.FieldTypes;

public class Mortality {

    private static final Map<String, IMortality> MORTALITY;
    static {
        MORTALITY = new LinkedHashMap<>();
    }

    private Mortality() {
        // no public instance
    }

    protected static void loadMortality(File mortalityFile) {

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(mortalityFile), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> csvDatasetFactory = new DataSetFactoryImplCsv(";");
            IDataSet<String, Long> csvDataSet = csvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> csvRecords = csvDataSet.getEntriesY();

            List<String> dateFields = csvDataSet.getKeysX().stream().skip(2).collect(Collectors.toList());

            for (IDataEntry<String, Long> csvRecord : csvRecords) {

                for (String dateField : dateFields) {

                    String[] yearAndWeekRaw = dateField.split("W");
                    int year = Integer.parseInt(yearAndWeekRaw[0]);
                    int week = Integer.parseInt(yearAndWeekRaw[1].substring(0));
                    if (year < 2010 || week == 0 || week == 99) {
                        continue;
                    }

                    String ageGroupRaw = csvRecord.optValue("AGE", FieldTypes.STRING).orElseThrow(() -> new RuntimeException("failed to resolve AGE"));
                    EAgeGroup ageGroup = EAgeGroup.optAgeGroupByName(ageGroupRaw).orElseThrow(() -> new RuntimeException("failed to resolve age group (" + ageGroupRaw + ")"));

                    String nuts3 = csvRecord.optValue("NUTS3", FieldTypes.STRING).orElseThrow(() -> new RuntimeException("failed to resolve NUTS3"));
                    String deathsRaw = csvRecord.optValue(dateField, FieldTypes.STRING).orElseThrow(() -> new RuntimeException("failed to resolve date (" + dateField + ")"));
                    int deaths = Integer.parseInt(deathsRaw);

                    MORTALITY.computeIfAbsent(nuts3, n -> new MortalityImpl()).addDeaths(ageGroup, toThursdayInWeek(year, week), deaths);
                    // MORTALITY.computeIfAbsent(nuts3, n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, toThursdayInWeek(year, week), deaths);

                }

            }

        } catch (Exception e) {
            throw new RuntimeException("failed to load mortality year", e);
        }

        // TODO read populations from yearly data, find region by mapped gzk and add populations

    }

    public static Optional<IMortality> optMortalityByNuts3(String nuts3, File mortalityFile) {
        if (MORTALITY.isEmpty()) {
            loadMortality(mortalityFile);
        }
        return Optional.ofNullable(MORTALITY.get(nuts3));
    }

    public static Date toThursdayInWeek(int year, int week) {
        LocalDate date = LocalDate.of(year, Month.JANUARY, 10);
        LocalDate dayInWeek = date.with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week);
        LocalDate localDate = dayInWeek.with(DayOfWeek.THURSDAY);
        return Date.from(localDate.atTime(8, 0).atZone(ZoneId.systemDefault()).toInstant());
    }

}
