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
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igorion.hexmap.Population;
import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IDataSet;
import com.igorion.report.dataset.IDataSetFactory;
import com.igorion.report.dataset.impl.DataSetFactoryImplCsv;
import com.igorion.report.value.FieldTypes;
import com.igorion.type.json.impl.JsonTypeImplHexmapDataRoot;

public class StatToPopParser {

    public static Map<String, String> KEYSET_MUNICIPALITY = new LinkedHashMap<>();
    static {
        KEYSET_MUNICIPALITY.put("#####", "Österreich");
        KEYSET_MUNICIPALITY.put("1####", "Burgenland");
        KEYSET_MUNICIPALITY.put("101##", "Eisenstadt(Stadt)");
        KEYSET_MUNICIPALITY.put("102##", "Rust(Stadt)");
        KEYSET_MUNICIPALITY.put("103##", "Eisenstadt-Umgebung");
        KEYSET_MUNICIPALITY.put("104##", "Güssing");
        KEYSET_MUNICIPALITY.put("105##", "Jennersdorf");
        KEYSET_MUNICIPALITY.put("106##", "Mattersburg");
        KEYSET_MUNICIPALITY.put("107##", "Neusiedl am See");
        KEYSET_MUNICIPALITY.put("108##", "Oberpullendorf");
        KEYSET_MUNICIPALITY.put("109##", "Oberwart");
        KEYSET_MUNICIPALITY.put("2####", "Kärnten");
        KEYSET_MUNICIPALITY.put("201##", "Klagenfurt Stadt");
        KEYSET_MUNICIPALITY.put("202##", "Villach Stadt");
        KEYSET_MUNICIPALITY.put("203##", "Hermagor");
        KEYSET_MUNICIPALITY.put("204##", "Klagenfurt Land");
        KEYSET_MUNICIPALITY.put("205##", "Sankt Veit an der Glan");
        KEYSET_MUNICIPALITY.put("206##", "Spittal an der Drau");
        KEYSET_MUNICIPALITY.put("207##", "Villach Land");
        KEYSET_MUNICIPALITY.put("208##", "Völkermarkt");
        KEYSET_MUNICIPALITY.put("209##", "Wolfsberg");
        KEYSET_MUNICIPALITY.put("210##", "Feldkirchen");
        KEYSET_MUNICIPALITY.put("3####", "Niederösterreich");
        KEYSET_MUNICIPALITY.put("301##", "Krems an der Donau(Stadt)");
        KEYSET_MUNICIPALITY.put("302##", "Sankt Pölten(Stadt)");
        KEYSET_MUNICIPALITY.put("303##", "Waidhofen an der Ybbs(Stadt)");
        KEYSET_MUNICIPALITY.put("304##", "Wiener Neustadt(Stadt)");
        KEYSET_MUNICIPALITY.put("305##", "Amstetten");
        KEYSET_MUNICIPALITY.put("306##", "Baden");
        KEYSET_MUNICIPALITY.put("307##", "Bruck an der Leitha");
        KEYSET_MUNICIPALITY.put("308##", "Gänserndorf");
        KEYSET_MUNICIPALITY.put("309##", "Gmünd");
        KEYSET_MUNICIPALITY.put("310##", "Hollabrunn");
        KEYSET_MUNICIPALITY.put("311##", "Horn");
        KEYSET_MUNICIPALITY.put("312##", "Korneuburg");
        KEYSET_MUNICIPALITY.put("313##", "Krems(Land)");
        KEYSET_MUNICIPALITY.put("314##", "Lilienfeld");
        KEYSET_MUNICIPALITY.put("315##", "Melk");
        KEYSET_MUNICIPALITY.put("316##", "Mistelbach");
        KEYSET_MUNICIPALITY.put("317##", "Mödling");
        KEYSET_MUNICIPALITY.put("318##", "Neunkirchen");
        KEYSET_MUNICIPALITY.put("319##", "Sankt Pölten(Land)");
        KEYSET_MUNICIPALITY.put("320##", "Scheibbs");
        KEYSET_MUNICIPALITY.put("321##", "Tulln");
        KEYSET_MUNICIPALITY.put("322##", "Waidhofen an der Thaya");
        KEYSET_MUNICIPALITY.put("323##", "Wiener Neustadt(Land)");
        KEYSET_MUNICIPALITY.put("325##", "Zwettl");
        KEYSET_MUNICIPALITY.put("4####", "Oberösterreich");
        KEYSET_MUNICIPALITY.put("401##", "Linz(Stadt)");
        KEYSET_MUNICIPALITY.put("402##", "Steyr(Stadt)");
        KEYSET_MUNICIPALITY.put("403##", "Wels(Stadt)");
        KEYSET_MUNICIPALITY.put("404##", "Braunau am Inn");
        KEYSET_MUNICIPALITY.put("405##", "Eferding");
        KEYSET_MUNICIPALITY.put("406##", "Freistadt");
        KEYSET_MUNICIPALITY.put("407##", "Gmunden");
        KEYSET_MUNICIPALITY.put("408##", "Grieskirchen");
        KEYSET_MUNICIPALITY.put("409##", "Kirchdorf an der Krems");
        KEYSET_MUNICIPALITY.put("410##", "Linz-Land");
        KEYSET_MUNICIPALITY.put("411##", "Perg");
        KEYSET_MUNICIPALITY.put("412##", "Ried im Innkreis");
        KEYSET_MUNICIPALITY.put("413##", "Rohrbach");
        KEYSET_MUNICIPALITY.put("414##", "Schärding");
        KEYSET_MUNICIPALITY.put("415##", "Steyr-Land");
        KEYSET_MUNICIPALITY.put("416##", "Urfahr-Umgebung");
        KEYSET_MUNICIPALITY.put("417##", "Vöcklabruck");
        KEYSET_MUNICIPALITY.put("418##", "Wels-Land");
        KEYSET_MUNICIPALITY.put("5####", "Salzburg");
        KEYSET_MUNICIPALITY.put("501##", "Salzburg(Stadt)");
        KEYSET_MUNICIPALITY.put("502##", "Hallein");
        KEYSET_MUNICIPALITY.put("503##", "Salzburg-Umgebung");
        KEYSET_MUNICIPALITY.put("504##", "Sankt Johann im Pongau");
        KEYSET_MUNICIPALITY.put("505##", "Tamsweg");
        KEYSET_MUNICIPALITY.put("506##", "Zell am See");
        KEYSET_MUNICIPALITY.put("6####", "Steiermark");
        KEYSET_MUNICIPALITY.put("601##", "Graz(Stadt)");
        KEYSET_MUNICIPALITY.put("603##", "Deutschlandsberg");
        KEYSET_MUNICIPALITY.put("606##", "Graz-Umgebung");
        KEYSET_MUNICIPALITY.put("610##", "Leibnitz");
        KEYSET_MUNICIPALITY.put("611##", "Leoben");
        KEYSET_MUNICIPALITY.put("612##", "Liezen");
        KEYSET_MUNICIPALITY.put("614##", "Murau");
        KEYSET_MUNICIPALITY.put("616##", "Voitsberg");
        KEYSET_MUNICIPALITY.put("617##", "Weiz");
        KEYSET_MUNICIPALITY.put("620##", "Murtal");
        KEYSET_MUNICIPALITY.put("621##", "Bruck-Mürzzuschlag");
        KEYSET_MUNICIPALITY.put("622##", "Hartberg-Fürstenfeld");
        KEYSET_MUNICIPALITY.put("623##", "Südoststeiermark");
        KEYSET_MUNICIPALITY.put("7####", "Tirol");
        KEYSET_MUNICIPALITY.put("701##", "Innsbruck-Stadt");
        KEYSET_MUNICIPALITY.put("702##", "Imst");
        KEYSET_MUNICIPALITY.put("703##", "Innsbruck-Land");
        KEYSET_MUNICIPALITY.put("704##", "Kitzbühel");
        KEYSET_MUNICIPALITY.put("705##", "Kufstein");
        KEYSET_MUNICIPALITY.put("706##", "Landeck");
        KEYSET_MUNICIPALITY.put("707##", "Lienz");
        KEYSET_MUNICIPALITY.put("708##", "Reutte");
        KEYSET_MUNICIPALITY.put("709##", "Schwaz");
        KEYSET_MUNICIPALITY.put("8####", "Vorarlberg");
        KEYSET_MUNICIPALITY.put("801##", "Bludenz");
        KEYSET_MUNICIPALITY.put("802##", "Bregenz");
        KEYSET_MUNICIPALITY.put("803##", "Dornbirn");
        KEYSET_MUNICIPALITY.put("804##", "Feldkirch");
        KEYSET_MUNICIPALITY.put("9####", "Wien");
//        KEYSET_MUNICIPALITY.put("900##", "Wien");
    }

    public static final Map<String, List<String>> AGE_CLASSIFICATION = new LinkedHashMap<>();
    static {

        // hexmap-base-population_05_99
//        AGE_CLASSIFICATION.put("00_04", Arrays.asList("0", "1", "2", "3", "4"));
//        AGE_CLASSIFICATION.put("00_99",
//                    Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
//                                "32", "33", "34", "35", "36",
//                                "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67",
//                                "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98",
//                                "99"));
//        AGE_CLASSIFICATION.put("05_99",
//                    Arrays.asList("5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36",
//                                "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67",
//                                "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98",
//                                "99"));

//        AGE_CLASSIFICATION.put("05_14", Arrays.asList("5", "6", "7", "8", "9", "10", "11", "12", "13", "14"));
//        AGE_CLASSIFICATION.put("15_24", Arrays.asList("15", "16", "17", "18", "19", "20", "21", "22", "23", "24"));
//        AGE_CLASSIFICATION.put("25_34", Arrays.asList("25", "26", "27", "28", "29", "30", "31", "32", "33", "34"));
//        AGE_CLASSIFICATION.put("35_44", Arrays.asList("35", "36", "37", "38", "39", "40", "41", "42", "43", "44"));
//        AGE_CLASSIFICATION.put("45_54", Arrays.asList("45", "46", "47", "48", "49", "50", "51", "52", "53", "54"));
//        AGE_CLASSIFICATION.put("55_64", Arrays.asList("55", "56", "57", "58", "59", "60", "61", "62", "63", "64"));
//        AGE_CLASSIFICATION.put("65_74", Arrays.asList("65", "66", "67", "68", "69", "70", "71", "72", "73", "74"));
//        AGE_CLASSIFICATION.put("75_84", Arrays.asList("75", "76", "77", "78", "79", "80", "81", "82", "83", "84"));
//        AGE_CLASSIFICATION.put("85_99", Arrays.asList("85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99"));

//        AGE_CLASSIFICATION.put("00_04", Arrays.asList("0", "1", "2", "3", "4"));
//        AGE_CLASSIFICATION.put("05_99",
//                    Arrays.asList("5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36",
//                                "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67",
//                                "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98",
//                                "99"));

        // hexcube-base-population_05n85
//        AGE_CLASSIFICATION.put("05_09", Arrays.asList("5", "6", "7", "8", "9"));
//        AGE_CLASSIFICATION.put("05_99",
//                    Arrays.asList("5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36",
//                                "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67",
//                                "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98",
//                                "99"));
//        AGE_CLASSIFICATION.put("10_14", Arrays.asList("10", "11", "12", "13", "14"));
//        AGE_CLASSIFICATION.put("15_19", Arrays.asList("15", "16", "17", "18", "19"));
//        AGE_CLASSIFICATION.put("20_24", Arrays.asList("20", "21", "22", "23", "24"));
//        AGE_CLASSIFICATION.put("25_29", Arrays.asList("25", "26", "27", "28", "29"));
//        AGE_CLASSIFICATION.put("30_34", Arrays.asList("30", "31", "32", "33", "34"));
//        AGE_CLASSIFICATION.put("35_39", Arrays.asList("35", "36", "37", "38", "39"));
//        AGE_CLASSIFICATION.put("40_44", Arrays.asList("40", "41", "42", "43", "44"));
//        AGE_CLASSIFICATION.put("45_49", Arrays.asList("45", "46", "47", "48", "49"));
//        AGE_CLASSIFICATION.put("50_54", Arrays.asList("50", "51", "52", "53", "54"));
//        AGE_CLASSIFICATION.put("55_59", Arrays.asList("55", "56", "57", "58", "59"));
//        AGE_CLASSIFICATION.put("60_64", Arrays.asList("60", "61", "62", "63", "64"));
//        AGE_CLASSIFICATION.put("65_69", Arrays.asList("65", "66", "67", "68", "69"));
//        AGE_CLASSIFICATION.put("70_74", Arrays.asList("70", "71", "72", "73", "74"));
//        AGE_CLASSIFICATION.put("75_79", Arrays.asList("75", "76", "77", "78", "79"));
//        AGE_CLASSIFICATION.put("80_84", Arrays.asList("80", "81", "82", "83", "84"));
//        AGE_CLASSIFICATION.put("85_99", Arrays.asList("85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99"));

        // hexcube-base-population_00n90
//        AGE_CLASSIFICATION.put("00_04", Arrays.asList("0", "1", "2", "3", "4"));
//        AGE_CLASSIFICATION.put("05_09", Arrays.asList("5", "6", "7", "8", "9"));
//        AGE_CLASSIFICATION.put("10_14", Arrays.asList("10", "11", "12", "13", "14"));
//        AGE_CLASSIFICATION.put("15_19", Arrays.asList("15", "16", "17", "18", "19"));
//        AGE_CLASSIFICATION.put("20_24", Arrays.asList("20", "21", "22", "23", "24"));
//        AGE_CLASSIFICATION.put("25_29", Arrays.asList("25", "26", "27", "28", "29"));
//        AGE_CLASSIFICATION.put("30_34", Arrays.asList("30", "31", "32", "33", "34"));
//        AGE_CLASSIFICATION.put("35_39", Arrays.asList("35", "36", "37", "38", "39"));
//        AGE_CLASSIFICATION.put("40_44", Arrays.asList("40", "41", "42", "43", "44"));
//        AGE_CLASSIFICATION.put("45_49", Arrays.asList("45", "46", "47", "48", "49"));
//        AGE_CLASSIFICATION.put("50_54", Arrays.asList("50", "51", "52", "53", "54"));
//        AGE_CLASSIFICATION.put("55_59", Arrays.asList("55", "56", "57", "58", "59"));
//        AGE_CLASSIFICATION.put("60_64", Arrays.asList("60", "61", "62", "63", "64"));
//        AGE_CLASSIFICATION.put("65_69", Arrays.asList("65", "66", "67", "68", "69"));
//        AGE_CLASSIFICATION.put("70_74", Arrays.asList("70", "71", "72", "73", "74"));
//        AGE_CLASSIFICATION.put("75_79", Arrays.asList("75", "76", "77", "78", "79"));
//        AGE_CLASSIFICATION.put("80_84", Arrays.asList("80", "81", "82", "83", "84"));
//        AGE_CLASSIFICATION.put("85_89", Arrays.asList("85", "86", "87", "88", "89"));
//        AGE_CLASSIFICATION.put("90_00", Arrays.asList("90", "91", "92", "93", "94", "95", "96", "97", "98", "99"));

        // hexcube-base-population_00_12_14n;
        AGE_CLASSIFICATION.put("00_11", Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
        AGE_CLASSIFICATION.put("12_14", Arrays.asList("12", "13", "14"));
        AGE_CLASSIFICATION.put("15_24", Arrays.asList("15", "16", "17", "18", "19", "20", "21", "22", "23", "24"));
        AGE_CLASSIFICATION.put("25_34", Arrays.asList("25", "26", "27", "28", "29", "30", "31", "32", "33", "34"));
        AGE_CLASSIFICATION.put("35_44", Arrays.asList("35", "36", "37", "38", "39", "40", "41", "42", "43", "44"));
        AGE_CLASSIFICATION.put("45_54", Arrays.asList("45", "46", "47", "48", "49", "50", "51", "52", "53", "54"));
        AGE_CLASSIFICATION.put("55_64", Arrays.asList("55", "56", "57", "58", "59", "60", "61", "62", "63", "64"));
        AGE_CLASSIFICATION.put("65_74", Arrays.asList("65", "66", "67", "68", "69", "70", "71", "72", "73", "74"));
        AGE_CLASSIFICATION.put("75_84", Arrays.asList("75", "76", "77", "78", "79", "80", "81", "82", "83", "84"));
        AGE_CLASSIFICATION.put("85_99", Arrays.asList("85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99"));

    }

    public static final int INDEX__TOTAL = AGE_CLASSIFICATION.size();
    public static final int INDEX_MEDIAN = AGE_CLASSIFICATION.size() + 1;

    // https://data.statistik.gv.at/data/OGD_bevstandjbab2002_BevStand_2002_HEADER.csv

    public static final File FOLDER__BASE = new File("C:\\privat\\_projects_cov\\covid2019_hexmap_data");
//    public static final File FILE_POP__IN = new File(FOLDER__BASE, "OGD_bevstandjbab2002_BevStand_2020.csv");
    public static final File FILE_POP_OUT_JSON = new File(FOLDER__BASE, "hexmap-data-population-gemeinde.json");
    public static final File FILE_POP_OUT__CSV = new File(FOLDER__BASE, "hexcube-base-population_00_12_14n.csv");

    public static final File FILE_POP_GKZ = new File(FOLDER__BASE, "endgueltige_bevoelkerungszahl_fuer_das_finanzjahr_2022_je_gemeinde.csv");
    public static final SimpleDateFormat DATE_FORMAT_____CASE = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) throws Exception {

        JsonTypeImplHexmapDataRoot dataRoot = new JsonTypeImplHexmapDataRoot("temp.json");
        for (int year = 2021; year <= 2021; year++) {
            parsePopulationData(year, dataRoot);
            System.out.println("done parsing data: " + year);
        }

//        int sum = 0;
//        for (int age = 0; age <= 100; age++) {
//            double val = dataRoot.getValue("01.01.2021", "50308", age);
//            System.out.println(age + " __ " + val);
//            sum += val;
//        }
//        System.out.println("all __ " + sum);

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_POP_GKZ), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> populationCsvDatasetFactory = new DataSetFactoryImplCsv();
            IDataSet<String, Long> populationCsvDataSet = populationCsvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> populationCsvRecords = populationCsvDataSet.getEntriesY();

            for (IDataEntry<String, Long> caseCsvRecord : populationCsvRecords) {

                String gkz = caseCsvRecord.optValue("gkz", FieldTypes.STRING).orElseThrow();
                String pg = caseCsvRecord.optValue("pg", FieldTypes.STRING).orElseThrow();

                KEYSET_MUNICIPALITY.put(gkz, pg);

            }

        }

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
        boolean addParents = true;

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

                if (firstIndexOfHash == -1 || addParents) {
                    csvOutputBuilder.append(String.format("%s;", DATE_FORMAT_____CASE.format(JsonTypeImplHexmapDataRoot.DATE_FORMAT_JSON.parse(dateKey))));
                    if (firstIndexOfHash == 0) {
                        csvOutputBuilder.append(String.format("%s;", "c")); // country
                    } else if (firstIndexOfHash == 1) {
                        csvOutputBuilder.append(String.format("%s;", "p")); // province
                    } else if (firstIndexOfHash == 3) {
                        csvOutputBuilder.append(String.format("%s;", "d")); // municipality
                    } else {
                        csvOutputBuilder.append(String.format("%s;", "m")); // district
                    }
                    csvOutputBuilder.append(String.format("%s;", key)); // .replace("#", "")
                    csvOutputBuilder.append(String.format("%s;", KEYSET_MUNICIPALITY.get(key)));
                }

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

                if (firstIndexOfHash == -1 || addParents) {

                    for (int classificationIndex = 0; classificationIndex < classificationKeys.size(); classificationIndex++) {
                        csvOutputBuilder.append(String.format("%s;", (int) fileRoot.getValue(dateKey, key, classificationIndex)));
                    }

                    if (addMedian) {
                        fileRoot.addData(dateKey, key, classificationKeys.size(), Math.round(median * 100) / 100D); // classificationKeys.size() + 1
                        csvOutputBuilder.append(String.format("%s;", fileRoot.getValue(dateKey, key, classificationKeys.size())));
                    }

                    csvOutputBuilder.append(String.format("%n"));

                }

//                fileRoot.addData(dateKey, key, classificationKeys.size(), total); // classificationKeys.size()
//                fileRoot.addData(dateKey, key, classificationKeys.size(), Math.round(median * 100) / 100D); // classificationKeys.size() + 1

            }

        }

        try (FileWriter csvWriter = new FileWriter(FILE_POP_OUT__CSV)) {
            csvWriter.write(csvOutputBuilder.toString());
        }

        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(FILE_POP_OUT_JSON, fileRoot); //

    }

    protected static void parsePopulationData(int year, JsonTypeImplHexmapDataRoot dataRoot) throws Exception {

        String dateKey = "01.01." + year;
        for (int age = 0; age <= 100; age++) {
            for (Entry<String, String> keysetProvinceEntry : KEYSET_MUNICIPALITY.entrySet()) {
                dataRoot.addData(dateKey, keysetProvinceEntry.getKey(), age, 0);
            }
        }

        File populationFile = new File(FOLDER__BASE, "OGD_bevstandjbab2002_BevStand_" + year + ".csv");

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(populationFile), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> popCsvDatasetFactory = new DataSetFactoryImplCsv();
            IDataSet<String, Long> popCsvDataSet = popCsvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> popCsvRecords = popCsvDataSet.getEntriesY();

            for (IDataEntry<String, Long> popCsvRecord : popCsvRecords) {

                // there is two entries per age (female, male), both should be accounted for

                String gkzRaw = popCsvRecord.optValue("C-GRGEMAKT-0", FieldTypes.STRING).orElseThrow();
                String gkz = gkzRaw.substring("GRGEMAKT-".length());
                if (Population.KEYSET_GKZ_OVERRIDE.containsKey(gkz)) {
                    gkz = Population.KEYSET_GKZ_OVERRIDE.get(gkz); // alternative naming for vienna districts
                }

                for (int age = 0; age <= 100; age++) {
                    dataRoot.addData(dateKey, gkz, age, 0);
                }

                String ageRaw = popCsvRecord.optValue("C-GALTEJ112-0", FieldTypes.STRING).orElseThrow();
                int age = Integer.parseInt(ageRaw.substring("GALTEJ112-".length())) - 1;

                int pop = popCsvRecord.optValue("F-ISIS-1", FieldTypes.LONG).orElseThrow().intValue();

//                if (gkz.equals("50308") && age == 10) {
//                    Math.random();
//                }
                dataRoot.addData(dateKey, gkz, age, pop);

                for (Entry<String, String> keysetProvinceEntry : KEYSET_MUNICIPALITY.entrySet()) {
                    if (keysetProvinceEntry.getKey().indexOf("#") >= 0) {
                        String prefixKey = keysetProvinceEntry.getKey().replaceAll("#", "");
                        if (gkz.startsWith(prefixKey)) {
//                            if (keysetProvinceEntry.getKey().equals("50308") && age == 10) {
//                                Math.random();
//                            }
                            dataRoot.addData(dateKey, keysetProvinceEntry.getKey(), age, pop);
                        }
                    }
                }

            } // for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords)

        }

    }

}
