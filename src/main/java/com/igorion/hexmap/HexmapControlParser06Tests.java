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
import java.util.List;
import java.util.Map;

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
import com.igorion.util.impl.Storage;

/**
 * reads the EMS csv file and creates a HEXMAP compatible structure from it
 *
 * @author h.fleischer
 * @since 19.02.2022
 *
 */
public class HexmapControlParser06Tests {

    public static final String TOTAL = "TOTAL";

    public static final String URL_TEST = "https://info.gesundheitsministerium.gv.at/data/timeline-faelle-bundeslaender.csv";

    public static final File FILE___TEST = new File(Storage.FOLDER___WORK, "tests_province.csv");
    public static final File FILE____POP = new File(Storage.FOLDER___WORK, "hexmap-base-population_00_99_median.csv");

    public static final SimpleDateFormat DATE_FORMAT_____CASE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

    public static void main(String[] args) throws Exception {

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
                if (gkz.indexOf("####") >= 0) {

                    gkz = gkz.substring(0, 1);

                    int population0099 = caseCsvRecord.optValue("00_99", FieldTypes.DOUBLE).orElseThrow().intValue();
                    double populationMedi = caseCsvRecord.optValue("median", FieldTypes.DOUBLE).orElseThrow();

                    population0099ByGkz.put(gkz, population0099);
                    populationMediByGkz.put(gkz, populationMedi);

                }

            }

        }

        JsonTypeImplHexmapDataRoot dataRoot = new JsonTypeImplHexmapDataRoot("temp.json");

        loadMiscData(URL_TEST, FILE___TEST);
        parseCaseData(dataRoot);

        JsonTypeImplHexmapDataRoot fileRoot = new JsonTypeImplHexmapDataRoot("hexmap-data-06-tests-province.json");
        fileRoot.addKeyset("Bundesland", Location.KEYSET_PROVINCE);
        fileRoot.addIdx("pcr", 0, 20000, false);
        fileRoot.addIdx("antigen", 0, 20000, false);
        fileRoot.addIdx("all", 0, 20000, false);
        fileRoot.setIndx(0);

        List<String> dateKeys = new ArrayList<>(dataRoot.getDateKeys());
        for (int i = 7; i < dateKeys.size(); i++) {

            String dateKey00 = dateKeys.get(i);
            String dateKeyM7 = dateKeys.get(i - 7);

            List<String> keys00 = new ArrayList<>(dataRoot.getKeys(dateKey00));
            keys00.sort((a, b) -> {
                return a.compareTo(b);
            });

            for (String key07 : keys00) {

                double valuePcr00 = dataRoot.getValue(dateKey00, key07, 0);
                double valuePcrM7 = dataRoot.getValue(dateKeyM7, key07, 0);

                double valueAg00 = dataRoot.getValue(dateKey00, key07, 1);
                double valueAgM7 = dataRoot.getValue(dateKeyM7, key07, 1);

                double valueAll00 = dataRoot.getValue(dateKey00, key07, 2);
                double valueAllM7 = dataRoot.getValue(dateKeyM7, key07, 2);

                double valuePos00 = dataRoot.getValue(dateKey00, key07, 3);
                double valuePosM7 = dataRoot.getValue(dateKeyM7, key07, 3);

                double population0099 = population0099ByGkz.get(key07); //  dataRoot.getValue(dateKey00, key07, 1);

                // double populationMedi = populationMediByGkz.get(key07); //  dataRoot.getValue(dateKey00, key07, 1);

                fileRoot.addData(dateKey00, key07, 0, (valuePos00 - valuePosM7) / (valuePcr00 - valuePcrM7));
                fileRoot.addData(dateKey00, key07, 1, (valuePos00 - valuePosM7) / (valueAg00 - valueAgM7));
                fileRoot.addData(dateKey00, key07, 2, (valuePos00 - valuePosM7) / (valueAll00 - valueAllM7));

//                fileRoot.addData(dateKey00, key07, 0, (valuePcr00 - valuePcrM7) / 0.00007 / population0099);
//                fileRoot.addData(dateKey00, key07, 1, (valueAg00 - valueAgM7) / 0.00007 / population0099);
//                fileRoot.addData(dateKey00, key07, 2, (valueAll00 - valueAllM7) / 0.00007 / population0099);
//
//                fileRoot.setPopulation(key07, population0099);

            }

        }

        Storage.store(fileRoot);

    }

    protected static String findProvinceKey(String province) {
        return Location.KEYSET_PROVINCE.entrySet().stream().filter(e -> e.getValue().equals(province)).map(e -> e.getKey()).findFirst().orElseThrow(() -> new RuntimeException("can not resolve: " + province));
    }

    protected static void parseCaseData(JsonTypeImplHexmapDataRoot dataRoot) throws Exception {

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE___TEST), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> caseCsvDatasetFactory = new DataSetFactoryImplCsv("(,|;|\t)");
            IDataSet<String, Long> caseCsvDataSet = caseCsvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> caseCsvRecords = caseCsvDataSet.getEntriesY();

            IFieldType<Date> fieldTypeDate = new FieldTypeImplDate(DATE_FORMAT_____CASE, "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\+[0-9]{2}:[0-9]{2}");

//            Map<String, Double> testsPcrLast = new HashMap<>();
            for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords) {

                Date entryDate = caseCsvRecord.optValue("Datum", fieldTypeDate).orElseThrow();
                String bkz = caseCsvRecord.optValue("BundeslandID", FieldTypes.STRING).orElseThrow();
                if (bkz.equals("10")) {
                    bkz = "#";
                }

                double testsPcrCurr = caseCsvRecord.optValue("TestungenPCR", FieldTypes.LONG).orElseThrow().intValue();
                double testsAgCurr = caseCsvRecord.optValue("TestungenAntigen", FieldTypes.LONG).orElseThrow().intValue();
                double testsAllCurr = caseCsvRecord.optValue("Testungen", FieldTypes.LONG).orElseThrow().intValue();
                double testsPositive = caseCsvRecord.optValue("BestaetigteFaelleBundeslaender", FieldTypes.LONG).orElseThrow().intValue();

                dataRoot.addData(entryDate, bkz, 0, testsPcrCurr);
                dataRoot.addData(entryDate, bkz, 1, testsAgCurr);
                dataRoot.addData(entryDate, bkz, 2, testsAllCurr);
                dataRoot.addData(entryDate, bkz, 3, testsPositive);

//                double testsPcrCurr = caseCsvRecord.optValue("TestungenPCR", FieldTypes.LONG).orElseThrow().intValue();
//                if (testsPcrLast.containsKey(bkz)) {
//                    dataRoot.addData(entryDate, bkz, 0, testsPcrCurr - testsPcrLast.get(bkz));
//                }
//                testsPcrLast.put(bkz, testsPcrCurr);

            } // for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords)

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
