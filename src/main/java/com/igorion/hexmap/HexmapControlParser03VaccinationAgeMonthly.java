package com.igorion.hexmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igorion.app.impl.C19Application;
import com.igorion.http.impl.OutboundHttpConfig;
import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IDataSet;
import com.igorion.report.dataset.IDataSetFactory;
import com.igorion.report.dataset.impl.DataSetFactoryImplCsv;
import com.igorion.report.value.FieldTypes;
import com.igorion.type.json.impl.JsonTypeImplHexmapDataRoot;

public class HexmapControlParser03VaccinationAgeMonthly {

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
        KEYSET_MUNICIPALITY.put("90101", "Innere Stadt");
        KEYSET_MUNICIPALITY.put("90201", "Leopoldstadt");
        KEYSET_MUNICIPALITY.put("90301", "Landstraße");
        KEYSET_MUNICIPALITY.put("90401", "Wieden");
        KEYSET_MUNICIPALITY.put("90501", "Margareten");
        KEYSET_MUNICIPALITY.put("90601", "Mariahilf");
        KEYSET_MUNICIPALITY.put("90701", "Neubau");
        KEYSET_MUNICIPALITY.put("90801", "Josefstadt");
        KEYSET_MUNICIPALITY.put("90901", "Alsergrund");
        KEYSET_MUNICIPALITY.put("91001", "Favoriten");
        KEYSET_MUNICIPALITY.put("91101", "Simmering");
        KEYSET_MUNICIPALITY.put("91201", "Meidling");
        KEYSET_MUNICIPALITY.put("91301", "Hietzing");
        KEYSET_MUNICIPALITY.put("91401", "Penzing");
        KEYSET_MUNICIPALITY.put("91501", "Rudolfsheim-Fünfhaus");
        KEYSET_MUNICIPALITY.put("91601", "Ottakring");
        KEYSET_MUNICIPALITY.put("91701", "Hernals");
        KEYSET_MUNICIPALITY.put("91801", "Währing");
        KEYSET_MUNICIPALITY.put("91901", "Döbling");
        KEYSET_MUNICIPALITY.put("92001", "Brigittenau");
        KEYSET_MUNICIPALITY.put("92101", "Floridsdorf");
        KEYSET_MUNICIPALITY.put("92201", "Donaustadt");
        KEYSET_MUNICIPALITY.put("92301", "Liesing");
    }

    public static final List<String> AGE_CLASSIFICATION = new ArrayList<>();
    static {
        AGE_CLASSIFICATION.add("05_09");
        AGE_CLASSIFICATION.add("10_14");
        AGE_CLASSIFICATION.add("15_19");
        AGE_CLASSIFICATION.add("20_24");
        AGE_CLASSIFICATION.add("25_29");
        AGE_CLASSIFICATION.add("30_34");
        AGE_CLASSIFICATION.add("35_39");
        AGE_CLASSIFICATION.add("40_44");
        AGE_CLASSIFICATION.add("45_49");
        AGE_CLASSIFICATION.add("50_54");
        AGE_CLASSIFICATION.add("55_59");
        AGE_CLASSIFICATION.add("60_64");
        AGE_CLASSIFICATION.add("65_69");
        AGE_CLASSIFICATION.add("70_74");
        AGE_CLASSIFICATION.add("75_79");
        AGE_CLASSIFICATION.add("80_84");
        AGE_CLASSIFICATION.add("85_99");
        AGE_CLASSIFICATION.add("05_99");
    }

//    public static final Map<String, String> KEYSET_MUNICIPIALITY = new LinkedHashMap<>();

    public static final long MIN_INSTANT_1 = 1627804800000L; // Date and time (GMT): Sunday, 1. August 2021 08:00:00
    public static final long MIN_INSTANT_2 = 1639900800000L; // Date and time (GMT): Sunday, 19. December 2021 08:00:00

    public static final File FOLDER_______BASE = new File("C:\\privat\\_projects_cov\\covid2019_hexmap_data");
    public static final File FILE___POPULATION = new File(FOLDER_______BASE, "hexcube-base-population_05n85.csv");
    public static final File FILE__VACCINATION_20220126 = new File(FOLDER_______BASE, "hexcube_status_by_district_20220126.csv");
    public static final File FILE__VACCINATION_20220222 = new File(FOLDER_______BASE, "hexcube_status_by_district_20220222.csv");

//    public static final SimpleDateFormat DATE_FORMAT___GITHUB = new SimpleDateFormat("yyyyMMdd");
//    public static final SimpleDateFormat DATE_FORMAT_____CASE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

    public static void main(String[] args) throws Exception {

        C19Application.init("");
        C19Application.getInstance().addSubConfig(OutboundHttpConfig.proxy("127.0.0.1", 8888));
        C19Application.getInstance().addSubConfig(OutboundHttpConfig.noopSsl());

        JsonTypeImplHexmapDataRoot dataRoot = new JsonTypeImplHexmapDataRoot("temp.json");

        // collect population
        Map<String, Map<String, Integer>> population0599ByGkz = new HashMap<>();
        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE___POPULATION), StandardCharsets.UTF_8))) {
            IDataSetFactory<String, Long> populationCsvDatasetFactory = new DataSetFactoryImplCsv();
            IDataSet<String, Long> populationCsvDataSet = populationCsvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> populationCsvRecords = populationCsvDataSet.getEntriesY();
            for (IDataEntry<String, Long> caseCsvRecord : populationCsvRecords) {

                String gkz = caseCsvRecord.optValue("gkz", FieldTypes.STRING).orElseThrow();
                if (!population0599ByGkz.containsKey(gkz)) {
                    population0599ByGkz.put(gkz, new HashMap<String, Integer>());
                }
                for (String ageClass : AGE_CLASSIFICATION) {
                    int ageCount = caseCsvRecord.optValue(ageClass, FieldTypes.LONG).orElseThrow(() -> new RuntimeException("failed to get population for field: " + ageClass)).intValue();
                    population0599ByGkz.get(gkz).put(ageClass, ageCount);
                }
            }
        }

        parseVaccData("26.01.2022", FILE__VACCINATION_20220126, dataRoot, population0599ByGkz);
        parseVaccData("22.02.2022", FILE__VACCINATION_20220222, dataRoot, population0599ByGkz);

//        String date20220126 = "26.01.2022"; FILE__VACCINATION_20220126
//        String date20220222 = "22.02.2022";
//        pars

        List<String> keys1 = new ArrayList<>(KEYSET_MUNICIPALITY.keySet());
        keys1.sort((a, b) -> {
            return a.compareTo(b);
        });
        Map<String, String> municipalityMapSorted = new LinkedHashMap<>();
        for (String key1 : keys1) {
            municipalityMapSorted.put(key1, KEYSET_MUNICIPALITY.get(key1));
        }

        JsonTypeImplHexmapDataRoot fileRoot = new JsonTypeImplHexmapDataRoot("hexmap-data-03-vacc-age.json");

        fileRoot.addKeyset("Bezirk", municipalityMapSorted);
        for (String ageClass : AGE_CLASSIFICATION) {
            fileRoot.addIdx(ageClass, 0, 1, false);
        }
        fileRoot.setIndx(0);

        List<String> dateKeys = new ArrayList<>(dataRoot.getDateKeys());

        for (int i = 0; i < dateKeys.size(); i++) {
            String dateKey = dateKeys.get(i);
            List<String> keys = new ArrayList<>(dataRoot.getKeys(dateKey));
            keys.sort((a, b) -> {
                return a.compareTo(b);
            });
            for (String key : keys) {

                for (int ageClassIndex = 0; ageClassIndex < AGE_CLASSIFICATION.size(); ageClassIndex++) {

                    String ageClass = AGE_CLASSIFICATION.get(ageClassIndex);

                    double population0599 = population0599ByGkz.get(key).get(ageClass);
                    double ageCountV = dataRoot.getValue(dateKey, key, ageClassIndex);
                    double ageCountVPercent = dataRoot.getValue(dateKey, key, ageClassIndex) / population0599;

                    fileRoot.addData(dateKey, key, ageClassIndex, Math.round(ageCountVPercent * 10000) / 10000D);

                }

            }

        }

        String hexmapFilePath = FOLDER_______BASE + "/hexmap-data-04-vacc-age.json";
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(hexmapFilePath), fileRoot); //

    }

    protected static void parseVaccData(String date, File file, JsonTypeImplHexmapDataRoot dataRoot, Map<String, Map<String, Integer>> population0599ByGkz) {

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> caseCsvDatasetFactory = new DataSetFactoryImplCsv();
            IDataSet<String, Long> caseCsvDataSet = caseCsvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> caseCsvRecords = caseCsvDataSet.getEntriesY();

            for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords) {

                String gkz = caseCsvRecord.optValue("GKZ", FieldTypes.STRING).orElseThrow(() -> new RuntimeException("failed to get value for GKZ"));

                if (Population.KEYSET_GKZ_OVERRIDE.containsKey(gkz)) {
                    gkz = Population.KEYSET_GKZ_OVERRIDE.get(gkz);
                }

                for (int ageClassIndex = 0; ageClassIndex < AGE_CLASSIFICATION.size(); ageClassIndex++) {

                    String ageClass = AGE_CLASSIFICATION.get(ageClassIndex);

                    if (!population0599ByGkz.containsKey(gkz)) {
                        System.err.println("missing gkz: " + gkz);
                    }
//                    int population = population0599ByGkz.get(gkz).get(ageClass);

                    double ageCountV = caseCsvRecord.optValue(ageClass + "_v", FieldTypes.LONG).orElseThrow(() -> new RuntimeException("failed to get value for field: " + ageClass + "_v")).doubleValue();
                    double ageCountVR = caseCsvRecord.optValue(ageClass + "_vr", FieldTypes.LONG).orElseThrow().doubleValue();
//                    double ageCountR = caseCsvRecord.optValue(ageClass + "_r", FieldTypes.LONG).orElseThrow().doubleValue();
//                    double ageCount = caseCsvRecord.optValue(ageClass, FieldTypes.LONG).orElseThrow().doubleValue();

                    if (gkz.equals("104##")) {
                        Math.random();
                    }

                    dataRoot.addData(date, gkz, ageClassIndex, ageCountV + ageCountVR);

                    for (Entry<String, String> keysetProvinceEntry : KEYSET_MUNICIPALITY.entrySet()) {
                        if (keysetProvinceEntry.getKey().indexOf("####") >= 0) {
                            String prefixKey = keysetProvinceEntry.getKey().replaceAll("#", "");
                            if (!gkz.equals(prefixKey) && gkz.startsWith(prefixKey)) {
                                KEYSET_MUNICIPALITY.put(keysetProvinceEntry.getKey(), keysetProvinceEntry.getValue());
                                dataRoot.addData(date, keysetProvinceEntry.getKey(), ageClassIndex, ageCountV + ageCountVR);
                            }
                        }
                    }

                }

            } // for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords)

        } catch (Exception ex) {
            System.err.println("failed to parse " + file);
            ex.printStackTrace();
        }

    }

}
