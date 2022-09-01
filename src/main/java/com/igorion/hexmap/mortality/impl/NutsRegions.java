package com.igorion.hexmap.mortality.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.igorion.hexmap.mortality.EAgeGroup;
import com.igorion.hexmap.mortality.INutsRegion;
import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IDataSet;
import com.igorion.report.dataset.IDataSetFactory;
import com.igorion.report.dataset.IFieldType;
import com.igorion.report.dataset.impl.DataSetFactoryImplCsv;
import com.igorion.report.value.FieldTypeImplDate;
import com.igorion.report.value.FieldTypes;

public class NutsRegions {

    private static final Map<String, INutsRegion> NUTS_REGIONS;
    public static final SimpleDateFormat DATE_FORMAT_____CASE = new SimpleDateFormat("yyyy-MM-dd");
    public static final IFieldType<Date> FIELD_TYPE_DATE_CASE = new FieldTypeImplDate(DATE_FORMAT_____CASE, "[0-9]{4}-[0-9]{2}-[0-9]{2}");

    static {
        NUTS_REGIONS = new LinkedHashMap<>();
    }

    private NutsRegions() {
        // no public instance
    }

    protected static void loadNutsRegions(File gkzPopulationFile, EAgeGroup... ageGroups) {

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(gkzPopulationFile), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> csvDatasetFactory = new DataSetFactoryImplCsv(";");
            IDataSet<String, Long> csvDataSet = csvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> csvRecords = csvDataSet.getEntriesY();

            for (IDataEntry<String, Long> csvRecord : csvRecords) {

                Date date = csvRecord.optValue("datum", FIELD_TYPE_DATE_CASE).orElseThrow(() -> new RuntimeException("failed to find datum"));
                String nuts = csvRecord.optValue("gkz", FieldTypes.STRING).orElseThrow();
                String name = csvRecord.optValue("gkn", FieldTypes.STRING).orElseGet(() -> "");
                INutsRegion nutsRegion = NUTS_REGIONS.computeIfAbsent(nuts, n -> new NutsRegionImpl(nuts, name));

                for (EAgeGroup ageGroup : ageGroups) {
                    if (ageGroup == EAgeGroup.ETOTAL) {
                        continue;
                    }
                    int population = csvRecord.optValue(ageGroup.getName(), FieldTypes.LONG).orElseThrow(() -> new RuntimeException("failed to find population for ageGroup (" + ageGroup + ")")).intValue();
                    nutsRegion.addPopulation(ageGroup, date, population);
                    nutsRegion.addPopulation(EAgeGroup.ETOTAL, date, population);
                }

            }

        } catch (Exception e) {
            throw new RuntimeException("failed to load nuts regions", e);
        }

        // TODO read populations from yearly data, find region by mapped gzk and add populations

    }

    public static Map<String, INutsRegion> getRegions(File gkzPopulationFile, EAgeGroup... ageGroups) {
        if (NUTS_REGIONS.isEmpty()) {
            loadNutsRegions(gkzPopulationFile, ageGroups);
        }
        return NUTS_REGIONS;
    }

}
