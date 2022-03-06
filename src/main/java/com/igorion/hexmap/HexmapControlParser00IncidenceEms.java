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
import java.util.Map.Entry;

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

/**
 * reads the EMS csv file and creates a HEXMAP compatible structure from it
 *
 * @author h.fleischer
 * @since 19.02.2022
 *
 */
public class HexmapControlParser00IncidenceEms {

    public static final String TOTAL = "TOTAL";

    public static final String URL_CASE = "https://info.gesundheitsministerium.gv.at/data/timeline-faelle-ems.csv";

    public static final File FILE___CASE = new File(Storage.FOLDER___WORK, "cases_ems.csv");
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

        loadMiscData(URL_CASE, FILE___CASE);
        parseCaseData(dataRoot);

        JsonTypeImplHexmapDataRoot fileRoot = new JsonTypeImplHexmapDataRoot("hexmap-data-00-incidence-ems.json");
        fileRoot.addKeyset("Bundesland", Location.KEYSET_PROVINCE);
        fileRoot.addIdx("cases", 0, 5000, false);
        fileRoot.setIndx(0);

        List<String> dateKeys = new ArrayList<>(dataRoot.getDateKeys());
        List<String> loggableDates = new ArrayList<>();
        loggableDates.add(dateKeys.get(dateKeys.size() - 1));
        loggableDates.add(dateKeys.get(dateKeys.size() - 8));
        loggableDates.add(dateKeys.get(dateKeys.size() - 15));

        for (int i = 7; i < dateKeys.size(); i++) {

            String dateKey00 = dateKeys.get(i);

            List<String> keys00 = new ArrayList<>(dataRoot.getKeys(dateKey00));
            keys00.sort((a, b) -> {
                return a.compareTo(b);
            });

            for (String key07 : keys00) {

                double value00 = dataRoot.getValue(dateKey00, key07, 0);
                double population0099 = population0099ByGkz.get(key07); //  dataRoot.getValue(dateKey00, key07, 1);
                double populationMedi = populationMediByGkz.get(key07); //  dataRoot.getValue(dateKey00, key07, 1);

                fileRoot.addData(dateKey00, key07, 0, value00);
                fileRoot.setPopulation(key07, population0099, populationMedi);

            }

        }

        Storage.store(fileRoot);
        for (Entry<String, String> locationEntry : Location.KEYSET_PROVINCE.entrySet()) {
            new IncidenceTweetFormatter(locationEntry.getKey(), locationEntry.getValue() + ", Fallzahlen laut EMS Morgenmeldung.").format(fileRoot);
        }
//        new IncidenceTweetFormatter("#", "Österreich, Fallzahlen laut EMS Morgenmeldung.").format(fileRoot);

    }

    protected static String findProvinceKey(String province) {
        return Location.KEYSET_PROVINCE.entrySet().stream().filter(e -> e.getValue().equals(province)).map(e -> e.getKey()).findFirst().orElseThrow(() -> new RuntimeException("can not resolve: " + province));
    }

    protected static void parseCaseData(JsonTypeImplHexmapDataRoot dataRoot) throws Exception {

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE___CASE), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> caseCsvDatasetFactory = new DataSetFactoryImplCsv();
            IDataSet<String, Long> caseCsvDataSet = caseCsvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> caseCsvRecords = caseCsvDataSet.getEntriesY();

            IFieldType<Date> fieldTypeDate = new FieldTypeImplDate(DATE_FORMAT_____CASE, "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\+[0-9]{2}:[0-9]{2}");

            for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords) {

                Date entryDate = caseCsvRecord.optValue("Datum", fieldTypeDate).orElseThrow();
                String bkz = caseCsvRecord.optValue("BundeslandID", FieldTypes.STRING).orElseThrow();
                if (bkz.equals("10")) {
                    bkz = "#";
                }

                double exposed = caseCsvRecord.optValue("BestaetigteFaelleEMS", FieldTypes.LONG).orElseThrow().intValue();
                dataRoot.addData(entryDate, bkz, 0, exposed);

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
