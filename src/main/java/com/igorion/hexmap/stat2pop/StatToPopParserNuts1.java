package com.igorion.hexmap.stat2pop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IDataSet;
import com.igorion.report.dataset.IDataSetFactory;
import com.igorion.report.dataset.impl.DataSetFactoryImplCsv;
import com.igorion.report.value.FieldTypes;
import com.igorion.type.json.impl.JsonTypeImplHexmapDataRoot;

public class StatToPopParserNuts1 {

    public static Map<String, String> KEYSET_MUNICIPALITY = new LinkedHashMap<>();
    static {

        KEYSET_MUNICIPALITY.put("AL", "");
        KEYSET_MUNICIPALITY.put("AT", "Austria");
        KEYSET_MUNICIPALITY.put("BE", "");
        KEYSET_MUNICIPALITY.put("BG", "");
        KEYSET_MUNICIPALITY.put("CH", "");
        KEYSET_MUNICIPALITY.put("CY", "");
        KEYSET_MUNICIPALITY.put("CZ", "");
        KEYSET_MUNICIPALITY.put("DE", "");
        KEYSET_MUNICIPALITY.put("DK", "");
        KEYSET_MUNICIPALITY.put("EE", "");
        KEYSET_MUNICIPALITY.put("EL", "");
        KEYSET_MUNICIPALITY.put("ES", "");
        KEYSET_MUNICIPALITY.put("FI", "");
        KEYSET_MUNICIPALITY.put("FR", "");
        KEYSET_MUNICIPALITY.put("HR", "");
        KEYSET_MUNICIPALITY.put("HU", "");
        KEYSET_MUNICIPALITY.put("IE", "");
        KEYSET_MUNICIPALITY.put("IS", "");
        KEYSET_MUNICIPALITY.put("IT", "");
        KEYSET_MUNICIPALITY.put("LI", "");
        KEYSET_MUNICIPALITY.put("LT", "");
        KEYSET_MUNICIPALITY.put("LU", "");
        KEYSET_MUNICIPALITY.put("LV", "");
        KEYSET_MUNICIPALITY.put("ME", "");
        KEYSET_MUNICIPALITY.put("MK", "");
        KEYSET_MUNICIPALITY.put("MT", "");
        KEYSET_MUNICIPALITY.put("NL", "");
        KEYSET_MUNICIPALITY.put("NO", "");
        KEYSET_MUNICIPALITY.put("PL", "");
        KEYSET_MUNICIPALITY.put("PT", "");
        KEYSET_MUNICIPALITY.put("RO", "");
        KEYSET_MUNICIPALITY.put("RS", "");
        KEYSET_MUNICIPALITY.put("SE", "");
        KEYSET_MUNICIPALITY.put("SI", "");
        KEYSET_MUNICIPALITY.put("SK", "");

    }

    public static final Map<String, List<String>> AGE_CLASSIFICATION = new LinkedHashMap<>();
    static {

        // hexcube-base-population_00n90
        AGE_CLASSIFICATION.put("00_04", Arrays.asList("0", "1", "2", "3", "4"));
        AGE_CLASSIFICATION.put("05_09", Arrays.asList("5", "6", "7", "8", "9"));
        AGE_CLASSIFICATION.put("10_14", Arrays.asList("10", "11", "12", "13", "14"));
        AGE_CLASSIFICATION.put("15_19", Arrays.asList("15", "16", "17", "18", "19"));
        AGE_CLASSIFICATION.put("20_24", Arrays.asList("20", "21", "22", "23", "24"));
        AGE_CLASSIFICATION.put("25_29", Arrays.asList("25", "26", "27", "28", "29"));
        AGE_CLASSIFICATION.put("30_34", Arrays.asList("30", "31", "32", "33", "34"));
        AGE_CLASSIFICATION.put("35_39", Arrays.asList("35", "36", "37", "38", "39"));
        AGE_CLASSIFICATION.put("40_44", Arrays.asList("40", "41", "42", "43", "44"));
        AGE_CLASSIFICATION.put("45_49", Arrays.asList("45", "46", "47", "48", "49"));
        AGE_CLASSIFICATION.put("50_54", Arrays.asList("50", "51", "52", "53", "54"));
        AGE_CLASSIFICATION.put("55_59", Arrays.asList("55", "56", "57", "58", "59"));
        AGE_CLASSIFICATION.put("60_64", Arrays.asList("60", "61", "62", "63", "64"));
        AGE_CLASSIFICATION.put("65_69", Arrays.asList("65", "66", "67", "68", "69"));
        AGE_CLASSIFICATION.put("70_74", Arrays.asList("70", "71", "72", "73", "74"));
        AGE_CLASSIFICATION.put("75_79", Arrays.asList("75", "76", "77", "78", "79"));
        AGE_CLASSIFICATION.put("80_84", Arrays.asList("80", "81", "82", "83", "84"));
        AGE_CLASSIFICATION.put("85_89", Arrays.asList("85", "86", "87", "88", "89"));
        AGE_CLASSIFICATION.put("90_00", Arrays.asList("90", "91", "92", "93", "94", "95", "96", "97", "98", "99"));

    }

    public static final int INDEX__TOTAL = AGE_CLASSIFICATION.size();
    public static final int INDEX_MEDIAN = AGE_CLASSIFICATION.size() + 1;

    // https://data.statistik.gv.at/data/OGD_bevstandjbab2002_BevStand_2002_HEADER.csv

    public static final File FOLDER__BASE = new File("C:\\privat\\_projects_cov\\covid2019_hexmap_data\\mortality");
//    public static final File FILE_POP__IN = new File(FOLDER__BASE, "OGD_bevstandjbab2002_BevStand_2020.csv");
//    public static final File FILE_POP_OUT_JSON = new File(FOLDER__BASE, "hexmap-data-population-gemeinde.json");
    public static final File FILE_POP_OUT__CSV = new File(FOLDER__BASE, "population_nuts1_00n90.csv");

    public static final File FILE_POP_IN___CSV = new File(FOLDER__BASE, "nuts1_populations.csv");
    public static final SimpleDateFormat DATE_FORMAT_____CASE = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) throws Exception {

        JsonTypeImplHexmapDataRoot dataRoot = new JsonTypeImplHexmapDataRoot("temp.json");
        parsePopulationData(dataRoot);

        Math.random();

//        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_POP_GKZ), StandardCharsets.UTF_8))) {
//
//            IDataSetFactory<String, Long> populationCsvDatasetFactory = new DataSetFactoryImplCsv();
//            IDataSet<String, Long> populationCsvDataSet = populationCsvDatasetFactory.createDataSet(csvReader);
//            List<IDataEntry<String, Long>> populationCsvRecords = populationCsvDataSet.getEntriesY();
//
//            for (IDataEntry<String, Long> caseCsvRecord : populationCsvRecords) {
//
//                String gkz = caseCsvRecord.optValue("gkz", FieldTypes.STRING).orElseThrow();
//                String pg = caseCsvRecord.optValue("pg", FieldTypes.STRING).orElseThrow();
//
//                KEYSET_MUNICIPALITY.put(gkz, pg);
//
//            }
//
//        }

        System.out.println("done reading pg");

        JsonTypeImplHexmapDataRoot fileRoot = new JsonTypeImplHexmapDataRoot("temp.json");

        List<String> keys1 = new ArrayList<>(KEYSET_MUNICIPALITY.keySet());
        keys1.sort((a, b) -> {
            return a.compareTo(b);
        });
        Map<String, String> municipalityMapSorted = new LinkedHashMap<>();
        for (String key1 : keys1) {
            municipalityMapSorted.put(key1, KEYSET_MUNICIPALITY.get(key1));
        }

        StringBuilder csvOutputBuilder = new StringBuilder();

        fileRoot.addKeyset("Gemeinde", municipalityMapSorted);

        csvOutputBuilder.append(String.format("%s;", "datum"));
        csvOutputBuilder.append(String.format("%s;", "gkt"));
        csvOutputBuilder.append(String.format("%s;", "gkz"));
        csvOutputBuilder.append(String.format("%s;", "gkn"));

        for (String ageClassification : AGE_CLASSIFICATION.keySet()) {
            fileRoot.addIdx(ageClassification, 0, 1, false);
            csvOutputBuilder.append(String.format("%s;", ageClassification));
        }

        boolean addMedian = false;
        boolean addTotal = false;

//        fileRoot.addIdx("gesamt;");
        if (addMedian) {
            fileRoot.addIdx("median", 30, 60, false);
            csvOutputBuilder.append(String.format("%s;", "median", false));
        }

        csvOutputBuilder.append(String.format("%n"));

        fileRoot.setIndx(AGE_CLASSIFICATION.size() + 1);

        System.out.println("done adding index");

        List<String> dateKeys = new ArrayList<>(dataRoot.getDateKeys());
        List<String> classificationKeys = new ArrayList<>(AGE_CLASSIFICATION.keySet());

        for (int i = 0; i < dateKeys.size(); i++) {

            String dateKey = dateKeys.get(i);
            List<String> keys = new ArrayList<>(dataRoot.getKeys(dateKey));
            keys.sort((a, b) -> {
                return a.compareTo(b);
            });

            // calculate total
            for (String key : keys) {
                int total = 0;
                for (int age = 0; age < 100; age++) {
                    double pop = dataRoot.getValue(dateKey, key, age);
                    total += pop;
                }
                if (addTotal) {
                    dataRoot.addData(dateKey, key, INDEX__TOTAL, total);
                }
                int semiTotal = 0;
                for (int age = 0; age < 100; age++) {
                    double pop = dataRoot.getValue(dateKey, key, age);
                    if (semiTotal + pop > total / 2) {
                        double fracA = semiTotal * 1D / total;
                        double fracB = (semiTotal + pop) * 1D / total;
                        double fracM = (0.5 - fracA) / (fracB - fracA);
                        if (addMedian) {
                            dataRoot.addData(dateKey, key, INDEX_MEDIAN, age + fracM);
                        }
                        break;
                    }
                    semiTotal += pop;
                }
            }

            for (String key : keys) {

                int firstIndexOfHash = key.indexOf("#");

                csvOutputBuilder.append(String.format("%s;", DATE_FORMAT_____CASE.format(JsonTypeImplHexmapDataRoot.DATE_FORMAT_JSON.parse(dateKey))));
                csvOutputBuilder.append(String.format("%s;", "c")); // country
                csvOutputBuilder.append(String.format("%s;", key)); // .replace("#", "")
                csvOutputBuilder.append(String.format("%s;", KEYSET_MUNICIPALITY.get(key)));

                double total = dataRoot.getValue(dateKey, key, INDEX__TOTAL);
                double median = dataRoot.getValue(dateKey, key, INDEX_MEDIAN);
                for (int age = 0; age < 100; age++) {

                    double pop = dataRoot.getValue(dateKey, key, age);
                    for (int classificationIndex = 0; classificationIndex < classificationKeys.size(); classificationIndex++) {
                        String classificationKey = classificationKeys.get(classificationIndex);
                        List<String> classificationValue = AGE_CLASSIFICATION.get(classificationKey);
                        if (classificationValue.contains(String.valueOf(age))) {
                            fileRoot.addData(dateKey, key, classificationIndex, pop); //  / total
//                            if (key.equals("50308")) {
//                                System.out.println("50308 (" + age + "): " + fileRoot.getValue("01.01.2021", "50308", classificationIndex));
//                            }

                        }
                    }

                }

                for (int classificationIndex = 0; classificationIndex < classificationKeys.size(); classificationIndex++) {
                    csvOutputBuilder.append(String.format("%s;", (int) fileRoot.getValue(dateKey, key, classificationIndex)));
                }

                if (addMedian) {
                    fileRoot.addData(dateKey, key, classificationKeys.size(), Math.round(median * 100) / 100D); // classificationKeys.size() + 1
                    csvOutputBuilder.append(String.format("%s;", fileRoot.getValue(dateKey, key, classificationKeys.size())));
                }

                csvOutputBuilder.append(String.format("%n"));

//                fileRoot.addData(dateKey, key, classificationKeys.size(), total); // classificationKeys.size()
//                fileRoot.addData(dateKey, key, classificationKeys.size(), Math.round(median * 100) / 100D); // classificationKeys.size() + 1

            }

        }

        try (FileWriter csvWriter = new FileWriter(FILE_POP_OUT__CSV)) {
            csvWriter.write(csvOutputBuilder.toString());
        }

//        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(FILE_POP_OUT_JSON, fileRoot); //

    }

    protected static void parsePopulationData(JsonTypeImplHexmapDataRoot dataRoot) throws Exception {

//        String dateKey = "01.01." + year;
//        for (int age = 0; age <= 100; age++) {
//            for (Entry<String, String> keysetProvinceEntry : KEYSET_MUNICIPALITY.entrySet()) {
//                dataRoot.addData(dateKey, keysetProvinceEntry.getKey(), age, 0);
//            }
//        }
//
//        File populationFile = new File(FOLDER__BASE, "OGD_bevstandjbab2002_BevStand_" + year + ".csv");

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_POP_IN___CSV), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> popCsvDatasetFactory = new DataSetFactoryImplCsv();
            IDataSet<String, Long> popCsvDataSet = popCsvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> popCsvRecords = popCsvDataSet.getEntriesY();

            for (IDataEntry<String, Long> popCsvRecord : popCsvRecords) {

                // there is two entries per age (female, male), both should be accounted for

                String nuts = popCsvRecord.optValue("nuts", FieldTypes.STRING).orElseThrow();
                if (nuts.equals("AL") || nuts.equals("HR") || nuts.equals("HU")) {
                    continue;
                }

                String ageRaw = popCsvRecord.optValue("age", FieldTypes.STRING).orElseThrow();
                int age = Integer.parseInt(ageRaw.substring("Y".length()));

                for (int year = 2002; year < 2022; year++) {
                    String popRaw = popCsvRecord.optValue(String.valueOf(year), FieldTypes.STRING).orElseThrow(() -> new RuntimeException(nuts));
                    if (StringUtils.isNotBlank(popRaw)) {
                        int pop = Integer.parseInt(popRaw);
                        String dateKey = "01.01." + year;
                        for (int agePad = 0; agePad <= 100; agePad++) {
                            dataRoot.addData(dateKey, nuts, agePad, 0);
                        }
                        dataRoot.addData(dateKey, nuts, age, pop);

                    }
                }

//                if (Population.KEYSET_GKZ_OVERRIDE.containsKey(gkz)) {
//                    gkz = Population.KEYSET_GKZ_OVERRIDE.get(gkz); // alternative naming for vienna districts
//                }
//
//                for (int age = 0; age <= 100; age++) {
//                    dataRoot.addData(dateKey, gkz, age, 0);
//                }

//                if (gkz.equals("50308") && age == 10) {
//                    Math.random();
//                }
//                dataRoot.addData(dateKey, gkz, age, pop);

//                for (Entry<String, String> keysetProvinceEntry : KEYSET_MUNICIPALITY.entrySet()) {
//                    if (keysetProvinceEntry.getKey().indexOf("#") >= 0) {
//                        String prefixKey = keysetProvinceEntry.getKey().replaceAll("#", "");
//                        if (gkz.startsWith(prefixKey)) {
////                            if (keysetProvinceEntry.getKey().equals("50308") && age == 10) {
////                                Math.random();
////                            }
//                            dataRoot.addData(dateKey, keysetProvinceEntry.getKey(), age, pop);
//                        }
//                    }
//                }

            } // for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords)

        }

    }

}
