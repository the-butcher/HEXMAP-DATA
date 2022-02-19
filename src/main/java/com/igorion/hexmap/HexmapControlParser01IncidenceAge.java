package com.igorion.hexmap;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igorion.app.impl.C19Application;
import com.igorion.hexmap.location.Location;
import com.igorion.hexmap.twitter.IncidenceTweetFormatter;
import com.igorion.http.IHttpRequest;
import com.igorion.http.IHttpResponse;
import com.igorion.http.impl.HttpRequest;
import com.igorion.http.impl.OutboundHttpConfig;
import com.igorion.http.impl.ResponseHandler;
import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IDataSet;
import com.igorion.report.dataset.IDataSetFactory;
import com.igorion.report.dataset.IFieldType;
import com.igorion.report.dataset.impl.DataSetFactoryImplCsv;
import com.igorion.report.value.FieldTypeImplDate;
import com.igorion.report.value.FieldTypes;
import com.igorion.type.json.impl.JsonTypeImplHexmapDataRoot;
import com.igorion.util.impl.Storage;

public class HexmapControlParser01IncidenceAge {

    public static final Map<String, String> CODES_BY_PROVINCE = new LinkedHashMap<>();
    static {
        CODES_BY_PROVINCE.put("Burgenland", "BGD");
        CODES_BY_PROVINCE.put("Kärnten", "KNT");
        CODES_BY_PROVINCE.put("Niederösterreich", "NOE");
        CODES_BY_PROVINCE.put("Oberösterreich", "OOE");
        CODES_BY_PROVINCE.put("Salzburg", "SBG");
        CODES_BY_PROVINCE.put("Steiermark", "STM");
        CODES_BY_PROVINCE.put("Tirol", "TIR");
        CODES_BY_PROVINCE.put("Vorarlberg", "VBG");
        CODES_BY_PROVINCE.put("Wien", "VIE");
        CODES_BY_PROVINCE.put("Österreich", "AUT");
    }

    public static final Map<String, String> AGE_GROUPS_CASE_MAP = new LinkedHashMap<>();
    static {
        AGE_GROUPS_CASE_MAP.put("<5", "<= 05");
        AGE_GROUPS_CASE_MAP.put("5-14", "05-14");
        AGE_GROUPS_CASE_MAP.put("15-24", "15-24");
        AGE_GROUPS_CASE_MAP.put("25-34", "25-34");
        AGE_GROUPS_CASE_MAP.put("35-44", "35-44");
        AGE_GROUPS_CASE_MAP.put("45-54", "45-54");
        AGE_GROUPS_CASE_MAP.put("55-64", "55-64");
        AGE_GROUPS_CASE_MAP.put("65-74", "65-74");
        AGE_GROUPS_CASE_MAP.put("75-84", "75-84");
        AGE_GROUPS_CASE_MAP.put(">84", ">= 85");
    }

    public static final String URL_CASE = "https://covid19-dashboard.ages.at/data/CovidFaelle_Altersgruppe.csv";

    public static final File FILE___CASE = new File(Storage.FOLDER___WORK, "cases_age.csv");

    public static final SimpleDateFormat DATE_FORMAT_____CASE = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static void main(String[] args) throws Exception {

        C19Application.init("");
        C19Application.getInstance().addSubConfig(OutboundHttpConfig.proxy("127.0.0.1", 8888));
        C19Application.getInstance().addSubConfig(OutboundHttpConfig.noopSsl());

        JsonTypeImplHexmapDataRoot dataRoot = new JsonTypeImplHexmapDataRoot("temp.json");

        loadMiscData(URL_CASE, FILE___CASE);
        parseCaseData(dataRoot);

        JsonTypeImplHexmapDataRoot fileRoot = new JsonTypeImplHexmapDataRoot("hexmap-data-01-incidence-age.json");
        fileRoot.addKeyset("Bundesland", Location.KEYSET_PROVINCE);
        fileRoot.addKeyset("Altersgruppe", Population.KEYSET_AGE_GROUP);
        fileRoot.addIdx("Fälle", 0, 5000);
        fileRoot.setIndx(0);

        List<String> dateKeys = new ArrayList<>(dataRoot.getDateKeys());

        for (int i = 7; i < dateKeys.size(); i++) {

            String dateKey00 = dateKeys.get(i);

            List<String> keys00 = new ArrayList<>(dataRoot.getKeys(dateKey00));
            keys00.sort((a, b) -> {
                return a.compareTo(b);
            });

            for (String key00 : keys00) {

                double value00 = dataRoot.getValue(dateKey00, key00, 0);
                double population = dataRoot.getValue(dateKey00, key00, 1);

                fileRoot.addData(dateKey00, key00, 0, value00);
                fileRoot.setPopulation(key00, (int) population);

            }

        }

        String hexmapFilePath = Storage.FOLDER_TARGET + "/hexmap-data-01-incidence-age.json";
        new ObjectMapper().writeValue(new File(hexmapFilePath), fileRoot); // .writerWithDefaultPrettyPrinter()

        new IncidenceTweetFormatter("##", "Österreich, Fallzahlen nach Altersgruppen.").format(fileRoot);

    }

    protected static String findProvinceKey(String province) {
        return Location.KEYSET_PROVINCE.entrySet().stream().filter(e -> e.getValue().equals(province)).map(e -> e.getKey()).findFirst().orElseThrow(() -> new RuntimeException("can not resolve: " + province));
    }

    protected static String findAgeGroupKey(String ageGroup) {
        return Population.KEYSET_AGE_GROUP.entrySet().stream().filter(e -> e.getValue().equals(ageGroup)).map(e -> e.getKey()).findFirst().orElseThrow(() -> new RuntimeException("can not resolve: " + ageGroup));
    }

    protected static void parseCaseData(JsonTypeImplHexmapDataRoot dataRoot) throws Exception {

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE___CASE), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> caseCsvDatasetFactory = new DataSetFactoryImplCsv();
            IDataSet<String, Long> caseCsvDataSet = caseCsvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> caseCsvRecords = caseCsvDataSet.getEntriesY();

            IFieldType<Date> fieldTypeDate = new FieldTypeImplDate(DATE_FORMAT_____CASE, "[0-9]{2}.[0-9]{2}.[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}");

            Map<String, Double> valuesByKey = new LinkedHashMap<>();
            Map<String, Double> populationByKey = new LinkedHashMap<>();
            Date currDate = new Date(0L);

            for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords) {

                Date entryDate = caseCsvRecord.optValue("Time", fieldTypeDate).orElseThrow();
                if (entryDate.getTime() != currDate.getTime()) {
                    if (entryDate.getTime() < currDate.getTime()) {
                        throw new IllegalStateException();
                    }
                    final Date dataData = currDate;
                    if (!valuesByKey.isEmpty()) {
                        valuesByKey.entrySet().stream().forEach(e -> dataRoot.addData(dataData, e.getKey(), 0, e.getValue()));
                        populationByKey.entrySet().stream().forEach(e -> dataRoot.addData(dataData, e.getKey(), 1, e.getValue()));
                    }
                    currDate = entryDate;
                    valuesByKey = new LinkedHashMap<>();
                    populationByKey = new LinkedHashMap<>();
                }

                String province = caseCsvRecord.optValue("Bundesland", FieldTypes.STRING).orElseThrow();
                String ageGroup = caseCsvRecord.optValue("Altersgruppe", FieldTypes.STRING).orElseThrow();

                if (CODES_BY_PROVINCE.containsKey(province) && AGE_GROUPS_CASE_MAP.containsKey(ageGroup)) {

                    double exposed = caseCsvRecord.optValue("Anzahl", FieldTypes.LONG).orElseThrow().intValue();
                    double population = caseCsvRecord.optValue("AnzEinwohner", FieldTypes.LONG).orElseThrow().intValue();

                    String key = findProvinceKey(province) + findAgeGroupKey(AGE_GROUPS_CASE_MAP.get(ageGroup));
                    String keyT = findProvinceKey(province) + findAgeGroupKey(Population.TOTAL);
                    if (!valuesByKey.containsKey(keyT)) {
//                        System.out.println("adding: " + keyT + " _ " + currDate);
                        valuesByKey.put(keyT, 0D);
                        populationByKey.put(keyT, 0D);
                    }
                    if (!valuesByKey.containsKey(key)) {

                        valuesByKey.put(key, 0D);
                        populationByKey.put(key, 0D);
                    }
                    valuesByKey.put(keyT, valuesByKey.get(keyT) + exposed);
                    valuesByKey.put(key, valuesByKey.get(key) + exposed);
                    populationByKey.put(keyT, populationByKey.get(keyT) + population);
                    populationByKey.put(key, populationByKey.get(key) + population);

                }

            } // for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords)

            final Date dataData = currDate;
            if (!valuesByKey.isEmpty()) {
                valuesByKey.entrySet().stream().forEach(e -> dataRoot.addData(dataData, e.getKey(), 0, e.getValue()));
                populationByKey.entrySet().stream().forEach(e -> dataRoot.addData(dataData, e.getKey(), 1, e.getValue()));
            }

        }

    }

    static void loadMiscData(String miscCsvUrl, File miscCsvFile) throws Exception {

        IHttpRequest<byte[]> dataRequest = HttpRequest.GET.create(miscCsvUrl, ResponseHandler.forRawContent());
        IHttpResponse<byte[]> dataResponse = dataRequest.send();
        byte[] miscCsvData = dataResponse.getEntity();

        try (InputStream inputStream = new ByteArrayInputStream(miscCsvData)) {

            byte[] buffer = new byte[2048];
            try (FileOutputStream fos = new FileOutputStream(miscCsvFile); BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)) {
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    bos.write(buffer, 0, len);
                }
            }

        }

    }

}
