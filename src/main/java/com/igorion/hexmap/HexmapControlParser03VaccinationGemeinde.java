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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igorion.app.impl.C19Application;
import com.igorion.hexmap.location.Location;
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

public class HexmapControlParser03VaccinationGemeinde {

//    public static final Map<String, String> KEYSET_MUNICIPIALITY = new LinkedHashMap<>();

    public static final String URL_TEMPLATE_1 = "https://github.com/statistikat/coronaDAT/blob/master/archive/%s/data/%s_235959_impfdaten_orig_csv_bmsgpk.zip?raw=true";
    public static final String URL_TEMPLATE_2 = "https://github.com/statistikat/coronaDAT/blob/master/archive/%s/data/%s_235959_impfdaten_orig2_csv_bmsgpk.zip?raw=true";
    public static final long MIN_INSTANT_1 = 1627804800000L; // Date and time (GMT): Sunday, 1. August 2021 08:00:00
    public static final long MIN_INSTANT_2 = 1639900800000L; // Date and time (GMT): Sunday, 19. December 2021 08:00:00

    public static final String FILE_TEMPLATE_ZIP = "vaccination_gemeinde_%s.zip";
    public static final String FILE_TEMPLATE_CSV = "vaccination_gemeinde_%s.csv";
    public static final String FILE___POPULATION = "hexmap-base-population_05_99.csv"; // "endgueltige_bevoelkerungszahl_fuer_das_finanzjahr_2022_je_gemeinde.csv";

    public static final SimpleDateFormat DATE_FORMAT___GITHUB = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat DATE_FORMAT_____CASE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

    public static void main(String[] args) throws Exception {

        C19Application.init("");
        C19Application.getInstance().addSubConfig(OutboundHttpConfig.proxy("127.0.0.1", 8888));
        C19Application.getInstance().addSubConfig(OutboundHttpConfig.noopSsl());

        JsonTypeImplHexmapDataRoot dataRoot = new JsonTypeImplHexmapDataRoot("temp.json");

        // build a map of GKZ and Gemeindename
//        loadMunicipalityData();

//        Location.getKeysetMunicipality();

        Map<String, Integer> population0099ByGkz = new HashMap<>();
        Map<String, Integer> population0599ByGkz = new HashMap<>();

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Storage.FOLDER___WORK, FILE___POPULATION)), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> populationCsvDatasetFactory = new DataSetFactoryImplCsv();
            IDataSet<String, Long> populationCsvDataSet = populationCsvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> populationCsvRecords = populationCsvDataSet.getEntriesY();

            for (IDataEntry<String, Long> caseCsvRecord : populationCsvRecords) {

                String gkz = caseCsvRecord.optValue("gkz", FieldTypes.STRING).orElseThrow();
                int population0099 = caseCsvRecord.optValue("00_99", FieldTypes.DOUBLE).orElseThrow().intValue();
                int population0599 = caseCsvRecord.optValue("05_99", FieldTypes.DOUBLE).orElseThrow().intValue();

                population0099ByGkz.put(gkz, population0099);
                population0599ByGkz.put(gkz, population0599);

            }

        }

        // load format 1
        for (long instant = MIN_INSTANT_1; instant < MIN_INSTANT_2; instant += DateUtil.MILLISECONDS_PER__DAY) {

            String formattedDateGithub = DATE_FORMAT___GITHUB.format(new Date(instant));
            String formattedGithubUrl1 = String.format(URL_TEMPLATE_1, formattedDateGithub, formattedDateGithub);
            File formattedFileZip = new File(Storage.FOLDER___WORK, String.format(FILE_TEMPLATE_ZIP, formattedDateGithub));
            File formattedFileCsv = new File(Storage.FOLDER___WORK, String.format(FILE_TEMPLATE_CSV, formattedDateGithub));

            if (!formattedFileCsv.exists()) {
                try {
                    loadMiscData(formattedGithubUrl1, formattedFileZip);
                    System.out.println(formattedGithubUrl1 + " > " + formattedFileZip);
                } catch (Exception ex) {
                    System.err.println(ex);
                }

            }

        }

        // load format 2
        for (long instant = MIN_INSTANT_2; instant <= System.currentTimeMillis(); instant += DateUtil.MILLISECONDS_PER__DAY) {

            String formattedDateGithub = DATE_FORMAT___GITHUB.format(new Date(instant));
            String formattedGithubUrl2 = String.format(URL_TEMPLATE_2, formattedDateGithub, formattedDateGithub);
            File formattedFileZip = new File(Storage.FOLDER___WORK, String.format(FILE_TEMPLATE_ZIP, formattedDateGithub));
            File formattedFileCsv = new File(Storage.FOLDER___WORK, String.format(FILE_TEMPLATE_CSV, formattedDateGithub));

            if (!formattedFileCsv.exists()) {
                try {
                    loadMiscData(formattedGithubUrl2, formattedFileZip);
                    System.out.println(formattedGithubUrl2 + " > " + formattedFileZip);
                } catch (Exception ex) {
                    System.err.println(ex);
                }

            }

        }

        // unzip either format
        for (long instant = MIN_INSTANT_1; instant <= System.currentTimeMillis(); instant += DateUtil.MILLISECONDS_PER__DAY) {

            String formattedDateGithub = DATE_FORMAT___GITHUB.format(new Date(instant));
            File formattedFileZip = new File(Storage.FOLDER___WORK, String.format(FILE_TEMPLATE_ZIP, formattedDateGithub));
            File formattedFileCsv = new File(Storage.FOLDER___WORK, String.format(FILE_TEMPLATE_CSV, formattedDateGithub));

            if (formattedFileZip.exists() && !formattedFileCsv.exists()) {

                byte[] buffer = new byte[2048];
                try (ZipInputStream zipInput = new ZipInputStream(new FileInputStream(formattedFileZip))) {

                    ZipEntry zipEntry = zipInput.getNextEntry();
                    if (zipEntry != null) {

                        try (FileOutputStream fos = new FileOutputStream(formattedFileCsv); BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)) {
                            int len;
                            while ((len = zipInput.read(buffer)) > 0) {
                                bos.write(buffer, 0, len);
                            }
                        }

                    }

                }

            }

        }

        int daySpacing = 7;

        // parse format 1
        for (long instant = MIN_INSTANT_1; instant < MIN_INSTANT_2; instant += DateUtil.MILLISECONDS_PER__DAY * daySpacing) {

            String formattedDateGithub = DATE_FORMAT___GITHUB.format(new Date(instant));
            File formattedFileCsv = new File(Storage.FOLDER___WORK, String.format(FILE_TEMPLATE_CSV, formattedDateGithub));

            if (!formattedFileCsv.exists()) {
                continue;
            }

            try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(formattedFileCsv), StandardCharsets.UTF_8))) {

                IDataSetFactory<String, Long> caseCsvDatasetFactory = new DataSetFactoryImplCsv();
                IDataSet<String, Long> caseCsvDataSet = caseCsvDatasetFactory.createDataSet(csvReader);
                List<IDataEntry<String, Long>> caseCsvRecords = caseCsvDataSet.getEntriesY();

                IFieldType<Date> fieldTypeDate = new FieldTypeImplDate(DATE_FORMAT_____CASE, "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\+[0-9]{2}:[0-9]{2}");

                Date currDate = new Date(0L);

                for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords) {

                    Date entryDate = caseCsvRecord.optValue("Datum", fieldTypeDate).orElseThrow();
                    String gkz = caseCsvRecord.optValue("Gemeindecode", FieldTypes.STRING).orElseThrow();

                    if (Population.KEYSET_GKZ_OVERRIDE.containsKey(gkz)) {
                        gkz = Population.KEYSET_GKZ_OVERRIDE.get(gkz);
                    }

                    int population = population0099ByGkz.get(gkz);

                    double dose1PerPop = caseCsvRecord.optValue("Teilgeimpfte", FieldTypes.DOUBLE).orElseThrow().doubleValue();
                    double dose2PerPop = caseCsvRecord.optValue("Vollimmunisierte", FieldTypes.DOUBLE).orElseThrow().doubleValue();

                    dataRoot.addData(entryDate, gkz, 0, dose1PerPop);
                    dataRoot.addData(entryDate, gkz, 1, dose2PerPop);
                    dataRoot.addData(entryDate, gkz, 2, 0);
                    dataRoot.addData(entryDate, gkz, 3, 0);
                    dataRoot.addData(entryDate, gkz, 4, population);

                    for (Entry<String, String> keysetProvinceEntry : Location.getKeysetMunicipality().entrySet()) {
                        if (keysetProvinceEntry.getKey().indexOf("#") >= 0) {
                            String prefixKey = keysetProvinceEntry.getKey().replaceAll("#", "");
                            if (!gkz.equals(prefixKey) && gkz.startsWith(prefixKey)) {
                                dataRoot.addData(entryDate, keysetProvinceEntry.getKey(), 0, dose1PerPop);
                                dataRoot.addData(entryDate, keysetProvinceEntry.getKey(), 1, dose2PerPop);
                                dataRoot.addData(entryDate, keysetProvinceEntry.getKey(), 2, 0); // 3rd dose
                                dataRoot.addData(entryDate, keysetProvinceEntry.getKey(), 3, 0); // certificates
                                dataRoot.addData(entryDate, keysetProvinceEntry.getKey(), 4, population);
                            }
                        }
                    }

                } // for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords)

            } catch (Exception ex) {
                System.err.println("failed to parse " + formattedFileCsv.getAbsolutePath());
                ex.printStackTrace();
            }

        }

        // parse format 2
        for (long instant = MIN_INSTANT_2; instant <= System.currentTimeMillis(); instant += DateUtil.MILLISECONDS_PER__DAY * 1) {

            String formattedDateGithub = DATE_FORMAT___GITHUB.format(new Date(instant));
            File formattedFileCsv = new File(Storage.FOLDER___WORK, String.format(FILE_TEMPLATE_CSV, formattedDateGithub));

            if (!formattedFileCsv.exists()) {
                continue;
            }

            try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(formattedFileCsv), StandardCharsets.UTF_8))) {

                IDataSetFactory<String, Long> caseCsvDatasetFactory = new DataSetFactoryImplCsv();
                IDataSet<String, Long> caseCsvDataSet = caseCsvDatasetFactory.createDataSet(csvReader);
                List<IDataEntry<String, Long>> caseCsvRecords = caseCsvDataSet.getEntriesY();

                IFieldType<Date> fieldTypeDate = new FieldTypeImplDate(DATE_FORMAT_____CASE, "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\+[0-9]{2}:[0-9]{2}");

                for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords) {

                    Date entryDate = caseCsvRecord.optValue("date", fieldTypeDate).orElseThrow();
                    String gkz = caseCsvRecord.optValue("municipality_id", FieldTypes.STRING).orElseThrow();

                    if (Population.KEYSET_GKZ_OVERRIDE.containsKey(gkz)) {
                        gkz = Population.KEYSET_GKZ_OVERRIDE.get(gkz);
                    }

                    int population = population0099ByGkz.get(gkz);

                    double dose1PerPop = caseCsvRecord.optValue("dose_1", FieldTypes.DOUBLE).orElseThrow().doubleValue();
                    double dose2PerPop = caseCsvRecord.optValue("dose_2", FieldTypes.DOUBLE).orElseThrow().doubleValue();
                    double dose3PerPop = caseCsvRecord.optValue("dose_3", FieldTypes.DOUBLE).orElseThrow().doubleValue();
                    double validCertificates = caseCsvRecord.optValue("valid_certificates", FieldTypes.DOUBLE).orElseThrow().doubleValue();

                    dataRoot.addData(entryDate, gkz, 0, dose1PerPop);
                    dataRoot.addData(entryDate, gkz, 1, dose2PerPop);
                    dataRoot.addData(entryDate, gkz, 2, dose3PerPop);
                    dataRoot.addData(entryDate, gkz, 3, validCertificates);
                    dataRoot.addData(entryDate, gkz, 4, population);

                    for (Entry<String, String> keysetProvinceEntry : Location.getKeysetMunicipality().entrySet()) {
                        if (keysetProvinceEntry.getKey().indexOf("#") >= 0) {
                            String prefixKey = keysetProvinceEntry.getKey().replaceAll("#", "");
                            if (!gkz.equals(prefixKey) && gkz.startsWith(prefixKey)) {
                                Location.getKeysetMunicipality().put(keysetProvinceEntry.getKey(), keysetProvinceEntry.getValue());
                                dataRoot.addData(entryDate, keysetProvinceEntry.getKey(), 0, dose1PerPop);
                                dataRoot.addData(entryDate, keysetProvinceEntry.getKey(), 1, dose2PerPop);
                                dataRoot.addData(entryDate, keysetProvinceEntry.getKey(), 2, dose3PerPop); // 3rd dose
                                dataRoot.addData(entryDate, keysetProvinceEntry.getKey(), 3, validCertificates); // certificates
                                dataRoot.addData(entryDate, keysetProvinceEntry.getKey(), 4, population);
                            }
                        }
                    }

                } // for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords)

            } catch (Exception ex) {
                System.err.println("failed to parse " + formattedFileCsv.getAbsolutePath());
                ex.printStackTrace();
            }

        }

        List<String> keys1 = new ArrayList<>(Location.getKeysetMunicipality().keySet());
        keys1.sort((a, b) -> {
            return a.compareTo(b);
        });
        Map<String, String> municipalityMapSorted = new LinkedHashMap<>();
        for (String key1 : keys1) {
            municipalityMapSorted.put(key1, Location.getKeysetMunicipality().get(key1));
        }

        JsonTypeImplHexmapDataRoot fileRoot = new JsonTypeImplHexmapDataRoot("hexmap-data-03-vacc-gemeinde.json");

        fileRoot.addKeyset("Gemeinde", municipalityMapSorted);
        fileRoot.addIdx("1.Dosis", 0, 1);
        fileRoot.addIdx("2.Dosis", 0, 1);
        fileRoot.addIdx("3.Dosis", 0, 1);
        fileRoot.addIdx("Zertifikate", 0, 1);
        fileRoot.setIndx(1);

        List<String> dateKeys = new ArrayList<>(dataRoot.getDateKeys());

        Set<String> praemieSet = new LinkedHashSet<>();

        for (int i = 0; i < dateKeys.size(); i++) {
            String dateKey = dateKeys.get(i);
            List<String> keys = new ArrayList<>(dataRoot.getKeys(dateKey));
            keys.sort((a, b) -> {
                return a.compareTo(b);
            });
            for (String key : keys) {

                double population0099 = population0099ByGkz.get(key); // dataRoot.getValue(dateKey, key, 4);
                double population0599 = population0599ByGkz.get(key); // dataRoot.getValue(dateKey, key, 4);

                double dose1 = dataRoot.getValue(dateKey, key, 0) / population0099;
                double dose2 = dataRoot.getValue(dateKey, key, 1) / population0099;
                double dose3 = dataRoot.getValue(dateKey, key, 2) / population0099;
                double certs = dataRoot.getValue(dateKey, key, 3) / population0599;

                if (dateKey.equals("10.02.2022") && certs >= 0.8) {
                    praemieSet.add(key);
                }

                fileRoot.addData(dateKey, key, 0, Math.round(dose1 * 10000) / 10000D);
                fileRoot.addData(dateKey, key, 1, Math.round(dose2 * 10000) / 10000D);
                fileRoot.addData(dateKey, key, 2, Math.round(dose3 * 10000) / 10000D);
                fileRoot.addData(dateKey, key, 3, Math.round(certs * 10000) / 10000D);

            }

        }

        String hexmapFilePath = Storage.FOLDER_TARGET + "/hexmap-data-03-vacc-gemeinde.json";
        new ObjectMapper().writeValue(new File(hexmapFilePath), fileRoot); // .writerWithDefaultPrettyPrinter().writerWithDefaultPrettyPrinter()

//        praemieSet.forEach(System.out::println);
        System.out.println("praemieSet: " + praemieSet.size());

    }

    static void loadMiscData(String miscCsvUrl, File miscCsvFile) throws Exception {

        IHttpRequest<byte[]> dataRequest = HttpRequest.GET.create(miscCsvUrl, ResponseHandler.forRawContent());
        IHttpResponse<byte[]> dataResponse = dataRequest.send();
        if (dataResponse.getStatusCode() >= 300) {
            throw new RuntimeException("failed to load from url (" + miscCsvUrl + ", " + dataResponse.getStatusCode() + ")");
        }
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

//    static void loadMunicipalityData() throws MalformedURLException {
//
//        // read feature
//        IHttpRequest.GET<JsonTypeImplQueryResultPoint> queryRequest = HttpRequest.GET.create("https://nbfleischer.int.vertigis.com/server/rest/services/pnt_full4/MapServer/0/query",
//                    ResponseHandler.forJsonTyped(JsonTypeImplQueryResultPoint.class));
//        queryRequest.setParameter("where", "1=1");
//        queryRequest.setParameter("outFields", "GKZ, PG");
//        queryRequest.setParameter("returnGeometry", "false");
//        queryRequest.setParameter("orderByFields", "GKZ");
//        queryRequest.setParameter("returnDistinctValues", "true");
//        queryRequest.setParameter("f", "pjson");
//
//        URL url = queryRequest.getUri().toURL();
//        try (InputStream columnInput = url.openConnection().getInputStream()) {
//
//            IDataSet<String, Long> datset = new DataSetFactoryImplFeature().createDataSet(columnInput, StandardCharsets.UTF_8);
//            List<IDataEntry<String, Long>> dataEntries = datset.getEntriesY();
//
//            for (IDataEntry<String, Long> dataEntry : dataEntries) {
//
//                Optional<String> oGkn = dataEntry.optValue("PG", FieldTypes.STRING);
//                if (oGkn.isPresent()) {
//
//                    String gkzString = dataEntry.optValue("GKZ", FieldTypes.STRING).orElseGet(() -> "-1");
//                    int gkzInteger = Integer.parseInt(gkzString);
//                    KEYSET_MUNICIPALITY.put(String.valueOf(gkzInteger), oGkn.get());
//
//                }
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

}
