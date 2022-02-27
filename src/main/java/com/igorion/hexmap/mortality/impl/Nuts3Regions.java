package com.igorion.hexmap.mortality.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.igorion.hexmap.mortality.EAgeGroup;
import com.igorion.hexmap.mortality.INuts3Region;
import com.igorion.hexmap.mortality.MortalityParserNuts3;
import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IDataSet;
import com.igorion.report.dataset.IDataSetFactory;
import com.igorion.report.dataset.IFieldType;
import com.igorion.report.dataset.impl.DataSetFactoryImplCsv;
import com.igorion.report.value.FieldTypeImplDate;
import com.igorion.report.value.FieldTypes;

public class Nuts3Regions {

    private static final Map<String, INuts3Region> NUTS3_REGIONS;
    public static final SimpleDateFormat DATE_FORMAT_____CASE = new SimpleDateFormat("yyyy-MM-dd");
    public static final IFieldType<Date> FIELD_TYPE_DATE_CASE = new FieldTypeImplDate(DATE_FORMAT_____CASE, "[0-9]{4}-[0-9]{2}-[0-9]{2}");

    static {
        NUTS3_REGIONS = new LinkedHashMap<>();
    }

    private Nuts3Regions() {
        // no public instance
    }

    protected static Optional<INuts3Region> optNuts3RegionByGkz(String gkz) {
        return NUTS3_REGIONS.values().stream().filter(r -> r.hasGkz(gkz)).findFirst();
    }

    protected static void loadNuts3Regions() {

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(MortalityParserNuts3.FILE_NUTS3______GKZ), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> csvDatasetFactory = new DataSetFactoryImplCsv(";");
            IDataSet<String, Long> csvDataSet = csvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> csvRecords = csvDataSet.getEntriesY();

            for (IDataEntry<String, Long> csvRecord : csvRecords) {
                // String nuts3 = csvRecord.optValue("NUTS3", FieldTypes.STRING).orElseThrow();
                String nuts3 = "AT";
                String gkz = csvRecord.optValue("GKZ", FieldTypes.STRING).orElseThrow();
                NUTS3_REGIONS.computeIfAbsent(nuts3, n -> new Nuts3RegionImpl(n)).mapGkz(gkz);
            }

        } catch (Exception e) {
            throw new RuntimeException("failed to load nuts3 regions", e);
        }

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(MortalityParserNuts3.FILE_GKZ_POPULATION), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> csvDatasetFactory = new DataSetFactoryImplCsv(";");
            IDataSet<String, Long> csvDataSet = csvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> csvRecords = csvDataSet.getEntriesY();

            for (IDataEntry<String, Long> csvRecord : csvRecords) {

                Date date = csvRecord.optValue("datum", FIELD_TYPE_DATE_CASE).orElseThrow();
                String gkz = csvRecord.optValue("gkz", FieldTypes.STRING).orElseThrow();
                if (gkz.startsWith("9")) {
                    gkz = "90001";
                }

                String _gkz = gkz;
                INuts3Region nuts3Region = optNuts3RegionByGkz(gkz).orElseThrow(() -> new RuntimeException("failed to find region for gkz (" + _gkz + ")"));

                for (EAgeGroup ageGroup : EAgeGroup.values()) {
                    int population = csvRecord.optValue(ageGroup.getName(), FieldTypes.LONG).orElseThrow(() -> new RuntimeException("failed to find population for ageGroup (" + ageGroup + ")")).intValue();
                    nuts3Region.addPopulation(ageGroup, date, population);
                }

            }

        } catch (Exception e) {
            throw new RuntimeException("failed to load nuts3 regions", e);
        }

        // TODO read populations from yearly data, find region by mapped gzk and add populations

    }

    public static List<INuts3Region> getRegions() {
        if (NUTS3_REGIONS.isEmpty()) {
            loadNuts3Regions();
        }
        return Collections.unmodifiableList(new ArrayList<>(NUTS3_REGIONS.values()));
    }

}
