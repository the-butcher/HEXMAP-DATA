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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import com.igorion.util.impl.DateUtil;
import com.igorion.util.impl.Storage;

public class HexmapControlParser02IncidenceBezirkDaily {

    public static final SimpleDateFormat DATE_FORMAT_____CASE = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT___GITHUB = new SimpleDateFormat("yyyyMMdd");

    public static final String URL_TEMPLATE = "https://raw.githubusercontent.com/statistikat/coronaDAT/master/archive/%s/data/%s_140201_orig_csv_ages.zip";
//    https://github.com/statistikat/coronaDAT/blob/master/archive/20220825/data/20220825_140201_orig_csv_ages.zip
//    https://github.com/statistikat/coronaDAT/raw/master/archive/20220901/data/20220901_140201_orig_csv_ages.zip
    public static final String FILE_TEMPLATE_ZIP = "cases_bezirk_%s.zip";
    public static final String FILE_TEMPLATE_CSV = "cases_bezirk_%s.csv";
    public static final String MIN_DATE_STRING = "01.07.2022 08:00:00"; // Date and time (GMT): Sunday, 19. December 2021 08:00:00fid

    public static final File FILE____POP = new File(Storage.FOLDER___WORK, "hexmap-base-population_00_99_median.csv");

    public static void main(String[] args) throws Exception {

        long minInstant = DATE_FORMAT_____CASE.parse(MIN_DATE_STRING).getTime();
        long maxInstant = System.currentTimeMillis();

        C19Application.init("");
        C19Application.getInstance().addSubConfig(OutboundHttpConfig.proxy("127.0.0.1", 8888));
        C19Application.getInstance().addSubConfig(OutboundHttpConfig.noopSsl());

        Map<String, Integer> population0099ByGkz = new HashMap<>();
        Map<String, Double> populationMediByGkz = new HashMap<>();

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE____POP)))) {

            IDataSetFactory<String, Long> populationCsvDatasetFactory = new DataSetFactoryImplCsv();
            IDataSet<String, Long> populationCsvDataSet = populationCsvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> populationCsvRecords = populationCsvDataSet.getEntriesY();

            for (IDataEntry<String, Long> caseCsvRecord : populationCsvRecords) {

                String gkz = caseCsvRecord.optValue("gkz", FieldTypes.STRING).orElseThrow();
                if (gkz.indexOf("##") >= 0) {

                    gkz = gkz.substring(0, 3);

                    int population0099 = caseCsvRecord.optValue("00_99", FieldTypes.DOUBLE).orElseThrow().intValue();
                    double populationMedi = caseCsvRecord.optValue("median", FieldTypes.DOUBLE).orElseThrow();

                    population0099ByGkz.put(gkz, population0099);
                    populationMediByGkz.put(gkz, populationMedi);

                }

            }

        }

        JsonTypeImplHexmapDataRoot dataRoot = new JsonTypeImplHexmapDataRoot("temp.json");

        // load format 1
        for (long instant = minInstant; instant <= maxInstant; instant += DateUtil.MILLISECONDS_PER__DAY) {

            String formattedDate = DATE_FORMAT___GITHUB.format(new Date(instant));
            String formattedGithubUrl1 = String.format(URL_TEMPLATE, formattedDate, formattedDate);
            File formattedFileZip = new File(Storage.FOLDER___WORK, String.format(FILE_TEMPLATE_ZIP, formattedDate));
            File formattedFileCsv = new File(Storage.FOLDER___WORK, String.format(FILE_TEMPLATE_CSV, formattedDate));

            if (!formattedFileZip.exists()) {
                try {
                    loadMiscData(formattedGithubUrl1, formattedFileZip);
                } catch (Exception ex) {
                    System.err.println(ex);
                }

            }

            if (!formattedFileCsv.exists()) {
                try {

                    byte[] buffer = new byte[2048];
                    try (ZipInputStream zipInput = new ZipInputStream(new FileInputStream(formattedFileZip))) {

                        ZipEntry zipEntry;
                        while ((zipEntry = zipInput.getNextEntry()) != null) {
                            if (zipEntry.getName().equalsIgnoreCase("CovidFaelle_Timeline_GKZ.csv")) {

                                try (FileOutputStream fos = new FileOutputStream(formattedFileCsv); BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)) {
                                    int len;
                                    while ((len = zipInput.read(buffer)) > 0) {
                                        bos.write(buffer, 0, len);
                                    }
                                }

                            }
                        }
                    }

                } catch (Exception ex) {
                    System.err.println(ex);
                }
            }

            String matchDateString = DATE_FORMAT___GITHUB.format(new Date(instant - DateUtil.MILLISECONDS_PER__DAY));
            System.out.println("matchDateString: " + matchDateString);

            try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(formattedFileCsv), StandardCharsets.UTF_8))) {

                IDataSetFactory<String, Long> caseCsvDatasetFactory = new DataSetFactoryImplCsv(";");
                IDataSet<String, Long> caseCsvDataSet = caseCsvDatasetFactory.createDataSet(csvReader);
                List<IDataEntry<String, Long>> caseCsvRecords = caseCsvDataSet.getEntriesY();

                IFieldType<Date> fieldTypeDate = new FieldTypeImplDate(DATE_FORMAT_____CASE, "[0-9]{2}.[0-9]{2}.[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}");

                for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords) {

                    Date entryDate = caseCsvRecord.optValue("Time", fieldTypeDate).orElseThrow();
                    String entryDateString = DATE_FORMAT___GITHUB.format(entryDate);

                    if (matchDateString.equals(entryDateString)) {

//                        System.out.println("matchDateString: " + matchDateString);

                        String gkz = caseCsvRecord.optValue("GKZ", FieldTypes.STRING).orElseThrow();
                        String district = caseCsvRecord.optValue("Bezirk", FieldTypes.STRING).orElseThrow();

                        if (Population.KEYSET_GKZ_OVERRIDE.containsKey(gkz)) {
                            gkz = Population.KEYSET_GKZ_OVERRIDE.get(gkz);
                        }

                        double cases = caseCsvRecord.optValue("AnzahlFaelleSum", FieldTypes.LONG).orElseThrow().intValue();
                        double fatal = caseCsvRecord.optValue("AnzahlTotSum", FieldTypes.LONG).orElseThrow().intValue();

                        dataRoot.addData(entryDate, gkz, 0, cases);
                        dataRoot.addData(entryDate, gkz, 1, fatal);

                        // find relevant province and add there
                        for (Entry<String, String> keysetProvinceEntry : Location.KEYSET_DISTRICT.entrySet()) {
                            if (keysetProvinceEntry.getKey().indexOf("#") >= 0) {
                                String prefixKey = keysetProvinceEntry.getKey().replaceAll("#", "");
                                if (!gkz.equals(keysetProvinceEntry.getKey()) && gkz.startsWith(prefixKey)) {
                                    dataRoot.addData(entryDate, keysetProvinceEntry.getKey(), 0, cases);
                                    dataRoot.addData(entryDate, keysetProvinceEntry.getKey(), 1, fatal);
                                }
                            }
                        }

                    }

                } // for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords)

            }

        }

//        parseCaseData(dataRoot);
//
        List<String> keys1 = new ArrayList<>(Location.KEYSET_DISTRICT.keySet());
        keys1.sort((a, b) -> {
            return a.compareTo(b);
        });

        JsonTypeImplHexmapDataRoot fileRoot = new JsonTypeImplHexmapDataRoot("hexmap-data-02-incidence-bezirk.json");

        Map<String, String> districtMapSorted = new LinkedHashMap<>();
        for (String key1 : keys1) {
            districtMapSorted.put(key1, Location.KEYSET_DISTRICT.get(key1));
        }

        fileRoot.addKeyset("Bezirk", districtMapSorted);
        fileRoot.addIdx("cases", 0, 7000, false);
        fileRoot.addIdx("fatal", 0, 7000, false);
        fileRoot.setIndx(0);

        List<String> dateKeys = new ArrayList<>(dataRoot.getDateKeys());

        for (int i = 7; i < dateKeys.size(); i++) {

            String dateKey00 = dateKeys.get(i);

            List<String> keys00 = new ArrayList<>(dataRoot.getKeys(dateKey00));
            keys00.sort((a, b) -> {
                return a.compareTo(b);
            });

            for (String key00 : keys00) {

                double cases00 = dataRoot.getValue(dateKey00, key00, 0);
                double fatal00 = dataRoot.getValue(dateKey00, key00, 1);
                double population0099 = population0099ByGkz.get(key00);
                double populationMedi = populationMediByGkz.get(key00);

                fileRoot.addData(dateKey00, key00, 0, cases00);
                fileRoot.addData(dateKey00, key00, 1, fatal00);
                fileRoot.setPopulation(key00, population0099, populationMedi);

            }

        }

        String hexmapFilePath = Storage.FOLDER_TARGET + "/hexmap-data-02-incidence-bezirk-daily.json";
        new ObjectMapper().writeValue(new File(hexmapFilePath), fileRoot); // .writerWithDefaultPrettyPrinter()

        Storage.store(fileRoot);
        new IncidenceTweetFormatter("###", "Österreich, Fallzahlen nach Bezirken laut AGES.").format(fileRoot);

        System.out.println("Der Tageswert berechnet sich aus der Differenz der Gesamtmeldung zur jeweils vorherigen Gesamtmeldung. \r\n"
                    + "\r\n"
                    + "Der am AGES Dashboard dargestellte Wert ist die Differenz der jeweils zugeordneten Fälle. Durch weiter zurückliegende Zuordnungen fällt dieser Wert stets etwas niedriger aus.");

    }

//    protected static void parseCaseData(JsonTypeImplHexmapDataRoot dataRoot) throws Exception {
//
//        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE___CASE), StandardCharsets.UTF_8))) {
//
//            IDataSetFactory<String, Long> caseCsvDatasetFactory = new DataSetFactoryImplCsv(";");
//            IDataSet<String, Long> caseCsvDataSet = caseCsvDatasetFactory.createDataSet(csvReader);
//            List<IDataEntry<String, Long>> caseCsvRecords = caseCsvDataSet.getEntriesY();
//
//            IFieldType<Date> fieldTypeDate = new FieldTypeImplDate(DATE_FORMAT_____CASE, "[0-9]{2}.[0-9]{2}.[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}");
//
//            for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords) {
//
//                Date entryDate = caseCsvRecord.optValue("Time", fieldTypeDate).orElseThrow();
//                String gkz = caseCsvRecord.optValue("GKZ", FieldTypes.STRING).orElseThrow();
//                String district = caseCsvRecord.optValue("Bezirk", FieldTypes.STRING).orElseThrow();
//
//                if (Population.KEYSET_GKZ_OVERRIDE.containsKey(gkz)) {
//                    gkz = Population.KEYSET_GKZ_OVERRIDE.get(gkz);
//                }
//
//                double cases = caseCsvRecord.optValue("AnzahlFaelleSum", FieldTypes.LONG).orElseThrow().intValue();
//                double fatal = caseCsvRecord.optValue("AnzahlTotSum", FieldTypes.LONG).orElseThrow().intValue();
//
//                dataRoot.addData(entryDate, gkz, 0, cases);
//                dataRoot.addData(entryDate, gkz, 1, fatal);
//
//                // find relevant province and add there
//                for (Entry<String, String> keysetProvinceEntry : Location.KEYSET_DISTRICT.entrySet()) {
//                    if (keysetProvinceEntry.getKey().indexOf("#") >= 0) {
//                        String prefixKey = keysetProvinceEntry.getKey().replaceAll("#", "");
//                        if (!gkz.equals(keysetProvinceEntry.getKey()) && gkz.startsWith(prefixKey)) {
//                            dataRoot.addData(entryDate, keysetProvinceEntry.getKey(), 0, cases);
//                            dataRoot.addData(entryDate, keysetProvinceEntry.getKey(), 1, fatal);
//                        }
//                    }
//                }
//
//            } // for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords)
//
//        }
//
//    }

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
