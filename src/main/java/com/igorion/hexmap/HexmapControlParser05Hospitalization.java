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
import java.util.Optional;

import com.igorion.app.impl.C19Application;
import com.igorion.hexmap.forecast.IForecast;
import com.igorion.hexmap.forecast.IForecasts;
import com.igorion.hexmap.forecast.impl.ForecastImpl;
import com.igorion.hexmap.forecast.impl.ForecastsImpl;
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

public class HexmapControlParser05Hospitalization {

    public static Map<String, Integer> KEYSET_REGULAR_BED_CAPACITY = new LinkedHashMap<>(); // ??
    static {

//        // KW05 (01.02.2022)
//        KEYSET_REGULAR_BED_CAPACITY.put("#", 37901);
//        KEYSET_REGULAR_BED_CAPACITY.put("1", 905);
//        KEYSET_REGULAR_BED_CAPACITY.put("2", 2489);
//        KEYSET_REGULAR_BED_CAPACITY.put("3", 6684);
//        KEYSET_REGULAR_BED_CAPACITY.put("4", 7110);
//        KEYSET_REGULAR_BED_CAPACITY.put("5", 2354);
//        KEYSET_REGULAR_BED_CAPACITY.put("6", 5148);
//        KEYSET_REGULAR_BED_CAPACITY.put("7", 3348);
//        KEYSET_REGULAR_BED_CAPACITY.put("8", 1861);
//        KEYSET_REGULAR_BED_CAPACITY.put("9", 7946);
//
//        // KW06 (08.02.2022)
//        KEYSET_REGULAR_BED_CAPACITY.put("#", 37865);
//        KEYSET_REGULAR_BED_CAPACITY.put("1", 905);
//        KEYSET_REGULAR_BED_CAPACITY.put("2", 2490);
//        KEYSET_REGULAR_BED_CAPACITY.put("3", 6667);
//        KEYSET_REGULAR_BED_CAPACITY.put("4", 7110);
//        KEYSET_REGULAR_BED_CAPACITY.put("5", 2369);
//        KEYSET_REGULAR_BED_CAPACITY.put("6", 5183);
//        KEYSET_REGULAR_BED_CAPACITY.put("7", 3332);
//        KEYSET_REGULAR_BED_CAPACITY.put("8", 1861);
//        KEYSET_REGULAR_BED_CAPACITY.put("9", 7946);
//
//        // KW07 (15.02.2022)
//        KEYSET_REGULAR_BED_CAPACITY.put("#", 37842);
//        KEYSET_REGULAR_BED_CAPACITY.put("1", 905);
//        KEYSET_REGULAR_BED_CAPACITY.put("2", 2484);
//        KEYSET_REGULAR_BED_CAPACITY.put("3", 6650);
//        KEYSET_REGULAR_BED_CAPACITY.put("4", 7110);
//        KEYSET_REGULAR_BED_CAPACITY.put("5", 2389);
//        KEYSET_REGULAR_BED_CAPACITY.put("6", 5183);
//        KEYSET_REGULAR_BED_CAPACITY.put("7", 3332);
//        KEYSET_REGULAR_BED_CAPACITY.put("8", 1861);
//        KEYSET_REGULAR_BED_CAPACITY.put("9", 7946);
//
//        // KW08 (22.02.2022)
//        KEYSET_REGULAR_BED_CAPACITY.put("#", 37842);
//        KEYSET_REGULAR_BED_CAPACITY.put("1", 905);
//        KEYSET_REGULAR_BED_CAPACITY.put("2", 2484);
//        KEYSET_REGULAR_BED_CAPACITY.put("3", 6621);
//        KEYSET_REGULAR_BED_CAPACITY.put("4", 7110);
//        KEYSET_REGULAR_BED_CAPACITY.put("5", 2391);
//        KEYSET_REGULAR_BED_CAPACITY.put("6", 5188);
//        KEYSET_REGULAR_BED_CAPACITY.put("7", 3336);
//        KEYSET_REGULAR_BED_CAPACITY.put("8", 1861);
//        KEYSET_REGULAR_BED_CAPACITY.put("9", 7946);
//
//        // KW09 (01.03.2022)
//        KEYSET_REGULAR_BED_CAPACITY.put("#", 37926);
//        KEYSET_REGULAR_BED_CAPACITY.put("1", 897);
//        KEYSET_REGULAR_BED_CAPACITY.put("2", 2484);
//        KEYSET_REGULAR_BED_CAPACITY.put("3", 6686);
//        KEYSET_REGULAR_BED_CAPACITY.put("4", 7110);
//        KEYSET_REGULAR_BED_CAPACITY.put("5", 2413);
//        KEYSET_REGULAR_BED_CAPACITY.put("6", 5213);
//        KEYSET_REGULAR_BED_CAPACITY.put("7", 3316);
//        KEYSET_REGULAR_BED_CAPACITY.put("8", 1861);
//        KEYSET_REGULAR_BED_CAPACITY.put("9", 7946);

//        // KW10 (08.03.2022)
//        KEYSET_REGULAR_BED_CAPACITY.put("#", 37838);
//        KEYSET_REGULAR_BED_CAPACITY.put("1", 893);
//        KEYSET_REGULAR_BED_CAPACITY.put("2", 2477);
//        KEYSET_REGULAR_BED_CAPACITY.put("3", 6623);
//        KEYSET_REGULAR_BED_CAPACITY.put("4", 7110);
//        KEYSET_REGULAR_BED_CAPACITY.put("5", 2422);
//        KEYSET_REGULAR_BED_CAPACITY.put("6", 5206);
//        KEYSET_REGULAR_BED_CAPACITY.put("7", 3300);
//        KEYSET_REGULAR_BED_CAPACITY.put("8", 1861);
//        KEYSET_REGULAR_BED_CAPACITY.put("9", 7946);

        // KW11 (15.03.2022)
//        double mult = 1.0;
//        KEYSET_REGULAR_BED_CAPACITY.put("#", (int) (37816 * mult));
//        KEYSET_REGULAR_BED_CAPACITY.put("1", (int) (893 * mult));  // Burgenland
//        KEYSET_REGULAR_BED_CAPACITY.put("2", (int) (2467 * mult)); // Kärnten
//        KEYSET_REGULAR_BED_CAPACITY.put("3", (int) (6634 * mult)); // Niederösterreich
//        KEYSET_REGULAR_BED_CAPACITY.put("4", (int) (7110 * mult)); // Oberösterreich
//        KEYSET_REGULAR_BED_CAPACITY.put("5", (int) (2393 * mult)); // Salzburg
//        KEYSET_REGULAR_BED_CAPACITY.put("6", (int) (5206 * mult)); // Steiermark
//        KEYSET_REGULAR_BED_CAPACITY.put("7", (int) (3306 * mult)); // Tirol
//        KEYSET_REGULAR_BED_CAPACITY.put("8", (int) (1861 * mult)); // Vorarlberg
//        KEYSET_REGULAR_BED_CAPACITY.put("9", (int) (7946 * mult)); // Wien

        double mult = 1.0;
        KEYSET_REGULAR_BED_CAPACITY.put("#", (int) (37772 * mult));
        KEYSET_REGULAR_BED_CAPACITY.put("1", (int) (893 * mult));  // Burgenland
        KEYSET_REGULAR_BED_CAPACITY.put("2", (int) (2473 * mult)); // Kärnten
        KEYSET_REGULAR_BED_CAPACITY.put("3", (int) (6596 * mult)); // Niederösterreich        
        KEYSET_REGULAR_BED_CAPACITY.put("4", (int) (7110 * mult)); // Oberösterreich
        KEYSET_REGULAR_BED_CAPACITY.put("5", (int) (2385 * mult)); // Salzburg
        KEYSET_REGULAR_BED_CAPACITY.put("6", (int) (5171 * mult)); // Steiermark
        KEYSET_REGULAR_BED_CAPACITY.put("7", (int) (3337 * mult)); // Tirol
        KEYSET_REGULAR_BED_CAPACITY.put("8", (int) (1861 * mult)); // Vorarlberg
        KEYSET_REGULAR_BED_CAPACITY.put("9", (int) (7946 * mult)); // Wien        

    }

    public static final String URL__HOSPITALIZATION = "https://covid19-dashboard.ages.at/data/Hospitalisierung.csv";

    public static final File FILE_HOSPITALIZATION = new File(Storage.FOLDER___WORK, "hopitalization_bezirk.csv");
    public static final File FILE____FORECAST_REG = new File(Storage.FOLDER___WORK, "forecast_reg_20220315.csv");
    public static final File FILE____FORECAST_ICU = new File(Storage.FOLDER___WORK, "forecast_icu_20220315.csv");

    // 24.01.2021 00:00:00;1;Burgenland;38;52;7;37;8;361192
    public static final SimpleDateFormat DATE_FORMAT_____FILE = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    public static final IFieldType<Date> FIELD_TYPE_DATE = new FieldTypeImplDate(DATE_FORMAT_____FILE, "[0-9]{2}.[0-9]{2}.[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}");

    public static void main(String[] args) throws Exception {

        C19Application.init("");
        C19Application.getInstance().addSubConfig(OutboundHttpConfig.proxy("127.0.0.1", 8888));
        C19Application.getInstance().addSubConfig(OutboundHttpConfig.noopSsl());

        JsonTypeImplHexmapDataRoot dataRoot = new JsonTypeImplHexmapDataRoot("temp.json");

        // build a map of GKZ and Gemeindename
        loadHospitalizationData();
        parseHospitalizationData(dataRoot);

        List<String> keys1 = new ArrayList<>(Location.KEYSET_PROVINCE.keySet());
        keys1.sort((a, b) -> {
            return a.compareTo(b);
        });

        JsonTypeImplHexmapDataRoot fileRootIcu = new JsonTypeImplHexmapDataRoot("hexmap-data-04-hospitalizazion-icu.json");
        JsonTypeImplHexmapDataRoot fileRootReg = new JsonTypeImplHexmapDataRoot("hexmap-data-05-hospitalizazion-reg.json");

        Map<String, String> districtMapSorted = new LinkedHashMap<>();
        for (String key1 : keys1) {
            districtMapSorted.put(key1, Location.KEYSET_PROVINCE.get(key1));
        }

        fileRootIcu.addKeyset("Bundesland", Location.KEYSET_PROVINCE);
        fileRootReg.addKeyset("Bundesland", Location.KEYSET_PROVINCE);

        fileRootIcu.addIdx("Grenzen", 0, 1, true); // 10
        fileRootIcu.addIdx("hsp__med1", 0, 1, true); // 25
        fileRootIcu.addIdx("hsp__med2", 0, 1, true); // 33
        fileRootIcu.addIdx("hsp__high", 0, 1, true); // 50
        fileRootIcu.addIdx("Intensivstation", 0, 1, false);
        fileRootIcu.addIdx("Prognose", 0, 1, true);
        fileRootIcu.addIdx("xlo____ln", 0, 1, true);
        fileRootIcu.addIdx("xhi____ln", 0, 1, true);
        fileRootIcu.setIndx(4);

        fileRootReg.addIdx("Grenzen", 0, 1, true); // 04
        fileRootReg.addIdx("hsp__med1", 0, 1, true); // 08
        fileRootReg.addIdx("hsp__med2", 0, 1, true); // 11
        fileRootReg.addIdx("hsp__high", 0, 1, true); // 30
        fileRootReg.addIdx("Normalstation", 0, 1, false);
        fileRootReg.addIdx("Prognose", 0, 1, true);
        fileRootReg.addIdx("xlo____ln", 0, 1, true);
        fileRootReg.addIdx("xhi____ln", 0, 1, true);
        fileRootReg.setIndx(4);

        List<String> dateKeys = new ArrayList<>(dataRoot.getDateKeys());

        for (int i = 0; i < dateKeys.size(); i++) {

            String dateKey00 = dateKeys.get(i);

            List<String> keys00 = new ArrayList<>(dataRoot.getKeys(dateKey00));
            keys00.sort((a, b) -> {
                return a.compareTo(b);
            });

            for (String key00 : keys00) {

                fileRootIcu.addData(dateKey00, key00, 0, 0.10);
                fileRootIcu.addData(dateKey00, key00, 1, 0.15);
                fileRootIcu.addData(dateKey00, key00, 2, 0.08);
                fileRootIcu.addData(dateKey00, key00, 3, 0.17);
                fileRootIcu.addData(dateKey00, key00, 4, dataRoot.getValue(dateKey00, key00, 0));
                fileRootIcu.addData(dateKey00, key00, 5, dataRoot.getValue(dateKey00, key00, 2));
                fileRootIcu.addData(dateKey00, key00, 6, dataRoot.getValue(dateKey00, key00, 3));
                fileRootIcu.addData(dateKey00, key00, 7, dataRoot.getValue(dateKey00, key00, 4));

                fileRootReg.addData(dateKey00, key00, 0, 0.04);
                fileRootReg.addData(dateKey00, key00, 1, 0.04);
                fileRootReg.addData(dateKey00, key00, 2, 0.03);
                fileRootReg.addData(dateKey00, key00, 3, 0.04);
                fileRootReg.addData(dateKey00, key00, 4, dataRoot.getValue(dateKey00, key00, 1));
                fileRootReg.addData(dateKey00, key00, 5, dataRoot.getValue(dateKey00, key00, 5));
                fileRootReg.addData(dateKey00, key00, 6, dataRoot.getValue(dateKey00, key00, 6));
                fileRootReg.addData(dateKey00, key00, 7, dataRoot.getValue(dateKey00, key00, 7));

            }

        }

        System.out.println(dateKeys.get(dateKeys.size() - 1));

        Storage.store(fileRootIcu, fileRootReg);

    }

    protected static IForecasts parseForecasts(File file) throws Exception {

        ForecastsImpl forecasts = new ForecastsImpl();
        try (BufferedReader forecastCsvReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> forecastCsvDatasetFactory = new DataSetFactoryImplCsv();
            IDataSet<String, Long> caseCsvDataSet = forecastCsvDatasetFactory.createDataSet(forecastCsvReader);
            List<IDataEntry<String, Long>> forecastCsvRecords = caseCsvDataSet.getEntriesY();

            for (IDataEntry<String, Long> forecastCsvRecord : forecastCsvRecords) {

                for (String location : Location.KEYSET_PROVINCE.keySet()) {

                    String fieldNameCi68Upper = "p84_" + location;
                    String fieldNameForecast = "p50_" + location;
                    String fieldNameCi68Lower = "p16_" + location;

                    Date entryDate = forecastCsvRecord.optValue("date", FIELD_TYPE_DATE).orElseThrow();
                    double ci68Upper = forecastCsvRecord.optValue(fieldNameCi68Upper, FieldTypes.DOUBLE).orElseThrow();
                    double forecast = forecastCsvRecord.optValue(fieldNameForecast, FieldTypes.DOUBLE).orElseThrow();
                    double ci68Lower = forecastCsvRecord.optValue(fieldNameCi68Lower, FieldTypes.DOUBLE).orElseThrow();

                    forecasts.addForecast(new ForecastImpl(entryDate, location, forecast, ci68Upper, ci68Lower));

                }

            }

        }
        return forecasts;

    }

    protected static void parseHospitalizationData(JsonTypeImplHexmapDataRoot dataRoot) throws Exception {

        Date maxDateHospital = new Date();

        IForecasts forecastsIcu = parseForecasts(FILE____FORECAST_ICU);
        IForecasts forecastsReg = parseForecasts(FILE____FORECAST_REG);

        Map<String, Integer> icuBedCapacity = new LinkedHashMap<>();

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_HOSPITALIZATION), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> caseCsvDatasetFactory = new DataSetFactoryImplCsv();
            IDataSet<String, Long> caseCsvDataSet = caseCsvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> caseCsvRecords = caseCsvDataSet.getEntriesY();

            for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords) {

                Date entryDate = caseCsvRecord.optValue("Meldedatum", FIELD_TYPE_DATE).orElseThrow();
                if (entryDate.getTime() > maxDateHospital.getTime()) {
                    maxDateHospital = entryDate;
                }

                String bkz = caseCsvRecord.optValue("BundeslandID", FieldTypes.STRING).orElseThrow();
                if (bkz.equals("10")) {
                    bkz = "#";
                }

                double totalIcu = caseCsvRecord.optValue("IntensivBettenKapGes", FieldTypes.LONG).orElseThrow().intValue();
                double curntIcu = caseCsvRecord.optValue("IntensivBettenBelCovid19", FieldTypes.LONG).orElseThrow().intValue();

                double curntReg = caseCsvRecord.optValue("NormalBettenBelCovid19", FieldTypes.LONG).orElseThrow().intValue();
                double totalReg = KEYSET_REGULAR_BED_CAPACITY.get(bkz);

                icuBedCapacity.put(bkz, (int) totalIcu);

                dataRoot.addData(entryDate, bkz, 0, Math.round(curntIcu * 10000D / totalIcu) / 10000D);
                dataRoot.addData(entryDate, bkz, 1, Math.round(curntReg * 10000D / totalReg) / 10000D);

                Optional<IForecast> oForecastIcu = forecastsIcu.optForecast(entryDate, bkz);
                if (oForecastIcu.isPresent()) {
                    IForecast forecast = oForecastIcu.get();
                    dataRoot.addData(entryDate, bkz, 2, Math.round(forecast.getForecast() * 10000D / totalIcu) / 10000D);
                    dataRoot.addData(entryDate, bkz, 3, Math.round(forecast.getCi68Lower() * 10000D / totalIcu) / 10000D);
                    dataRoot.addData(entryDate, bkz, 4, Math.round((forecast.getCi68Upper() - forecast.getCi68Lower()) * 10000D / totalIcu) / 10000D);
                } else {
                    dataRoot.addData(entryDate, bkz, 2, 0);
                    dataRoot.addData(entryDate, bkz, 3, 0);
                    dataRoot.addData(entryDate, bkz, 4, 0);
                }

                Optional<IForecast> oForecastReg = forecastsReg.optForecast(entryDate, bkz);
                if (oForecastReg.isPresent()) {
                    IForecast forecast = oForecastReg.get();
                    dataRoot.addData(entryDate, bkz, 5, Math.round(forecast.getForecast() * 10000D / totalReg) / 10000D);
                    dataRoot.addData(entryDate, bkz, 6, Math.round(forecast.getCi68Lower() * 10000D / totalReg) / 10000D);
                    dataRoot.addData(entryDate, bkz, 7, Math.round((forecast.getCi68Upper() - forecast.getCi68Lower()) * 10000D / totalReg) / 10000D);
                } else {
                    dataRoot.addData(entryDate, bkz, 5, 0);
                    dataRoot.addData(entryDate, bkz, 6, 0);
                    dataRoot.addData(entryDate, bkz, 7, 0);
                }

            } // for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords)

            long maxForecastInstant = Math.max(forecastsIcu.getMaxDate().getTime(), forecastsReg.getMaxDate().getTime());

            for (long instant = maxDateHospital.getTime() + DateUtil.MILLISECONDS_PER__DAY; instant <= maxForecastInstant + DateUtil.MILLISECONDS_PER__DAY; instant += DateUtil.MILLISECONDS_PER__DAY) {

                Date entryDate = new Date(instant);

                for (String bkz : Location.KEYSET_PROVINCE.keySet()) {

                    double totalIcu = icuBedCapacity.get(bkz);
                    double totalReg = KEYSET_REGULAR_BED_CAPACITY.get(bkz);

                    Optional<IForecast> oForecastIcu = forecastsIcu.optForecast(entryDate, bkz);
                    Optional<IForecast> oForecastReg = forecastsReg.optForecast(entryDate, bkz);
                    if (oForecastIcu.isPresent() && oForecastReg.isPresent()) {

//                        System.out.println("extra forecast: " + entryDate);
                        IForecast forecastIcu = oForecastIcu.get();
                        IForecast forecastReg = oForecastReg.get();

                        dataRoot.addData(entryDate, bkz, 0, 0);
                        dataRoot.addData(entryDate, bkz, 1, 0);
                        dataRoot.addData(entryDate, bkz, 2, Math.round(forecastIcu.getForecast() * 10000D / totalIcu) / 10000D);
                        dataRoot.addData(entryDate, bkz, 3, Math.round(forecastIcu.getCi68Lower() * 10000D / totalIcu) / 10000D);
                        dataRoot.addData(entryDate, bkz, 4, Math.round((forecastIcu.getCi68Upper() - forecastIcu.getCi68Lower()) * 10000D / totalIcu) / 10000D);
                        dataRoot.addData(entryDate, bkz, 5, Math.round(forecastReg.getForecast() * 10000D / totalReg) / 10000D);
                        dataRoot.addData(entryDate, bkz, 6, Math.round(forecastReg.getCi68Lower() * 10000D / totalReg) / 10000D);
                        dataRoot.addData(entryDate, bkz, 7, Math.round((forecastReg.getCi68Upper() - forecastReg.getCi68Lower()) * 10000D / totalReg) / 10000D);

                    } else {
                        System.out.println("extra forecast missing: " + entryDate);
                    }
                }

            }

        }

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

    static void loadHospitalizationData() throws Exception {
        loadMiscData(URL__HOSPITALIZATION, FILE_HOSPITALIZATION);
    }

}
