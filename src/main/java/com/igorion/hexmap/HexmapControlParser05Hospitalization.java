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

public class HexmapControlParser05Hospitalization {

    public static Map<String, Integer> KEYSET_REGULAR_BED_CAPACITY = new LinkedHashMap<>();
    static {
        KEYSET_REGULAR_BED_CAPACITY.put("#", 37869);
        KEYSET_REGULAR_BED_CAPACITY.put("1", 905);
        KEYSET_REGULAR_BED_CAPACITY.put("2", 2484);
        KEYSET_REGULAR_BED_CAPACITY.put("3", 6650);
        KEYSET_REGULAR_BED_CAPACITY.put("4", 7110);
        KEYSET_REGULAR_BED_CAPACITY.put("5", 2398);
        KEYSET_REGULAR_BED_CAPACITY.put("6", 5183);
        KEYSET_REGULAR_BED_CAPACITY.put("7", 3332);
        KEYSET_REGULAR_BED_CAPACITY.put("8", 1861);
        KEYSET_REGULAR_BED_CAPACITY.put("9", 7946);
    }

    public static final String URL__HOSPITALIZATION = "https://covid19-dashboard.ages.at/data/Hospitalisierung.csv";

    public static final File FILE_HOSPITALIZATION = new File(Storage.FOLDER___WORK, "hopitalization_bezirk.csv");

    // 24.01.2021 00:00:00;1;Burgenland;38;52;7;37;8;361192
    public static final SimpleDateFormat DATE_FORMAT_____FILE = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

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

        fileRootIcu.addIdx("hsp___low", 0, 1); // 10
        fileRootIcu.addIdx("hsp__med1", 0, 1); // 25
        fileRootIcu.addIdx("hsp__med2", 0, 1); // 33
        fileRootIcu.addIdx("hsp__high", 0, 1); // 50
        fileRootIcu.addIdx("Intensivstation", 0, 1);
        fileRootIcu.setIndx(4);

        fileRootReg.addIdx("hsp___low", 0, 1); // 04
        fileRootReg.addIdx("hsp__med1", 0, 1); // 08
        fileRootReg.addIdx("hsp__med2", 0, 1); // 11
        fileRootReg.addIdx("hsp__high", 0, 1); // 30
        fileRootReg.addIdx("Normalstation", 0, 1);
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

                fileRootReg.addData(dateKey00, key00, 0, 0.04);
                fileRootReg.addData(dateKey00, key00, 1, 0.04);
                fileRootReg.addData(dateKey00, key00, 2, 0.03);
                fileRootReg.addData(dateKey00, key00, 3, 0.19);
                fileRootReg.addData(dateKey00, key00, 4, dataRoot.getValue(dateKey00, key00, 1));

            }

        }

        System.out.println(dateKeys.get(dateKeys.size() - 1));

        Storage.store(fileRootIcu);
        Storage.store(fileRootReg);

    }

    protected static void parseHospitalizationData(JsonTypeImplHexmapDataRoot dataRoot) throws Exception {

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_HOSPITALIZATION), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> caseCsvDatasetFactory = new DataSetFactoryImplCsv();
            IDataSet<String, Long> caseCsvDataSet = caseCsvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> caseCsvRecords = caseCsvDataSet.getEntriesY();

            IFieldType<Date> fieldTypeDate = new FieldTypeImplDate(DATE_FORMAT_____FILE, "[0-9]{2}.[0-9]{2}.[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}");

            for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords) {

                Date entryDate = caseCsvRecord.optValue("Meldedatum", fieldTypeDate).orElseThrow();
                String bkz = caseCsvRecord.optValue("BundeslandID", FieldTypes.STRING).orElseThrow();
                if (bkz.equals("10")) {
                    bkz = "#";
                }

                double totalIcu = caseCsvRecord.optValue("IntensivBettenKapGes", FieldTypes.LONG).orElseThrow().intValue();
                double curntIcu = caseCsvRecord.optValue("IntensivBettenBelCovid19", FieldTypes.LONG).orElseThrow().intValue();

                double curntReg = caseCsvRecord.optValue("NormalBettenBelCovid19", FieldTypes.LONG).orElseThrow().intValue();
                double totalReg = KEYSET_REGULAR_BED_CAPACITY.get(bkz);

//                dataRoot.addData(entryDate, bkz, 0, 0.10);
//                dataRoot.addData(entryDate, bkz, 1, 0.23);
//                dataRoot.addData(entryDate, bkz, 2, 0.17);
                dataRoot.addData(entryDate, bkz, 0, Math.round(curntIcu * 10000D / totalIcu) / 10000D);
                dataRoot.addData(entryDate, bkz, 1, Math.round(curntReg * 10000D / totalReg) / 10000D);

            } // for (IDataEntry<String, Long> caseCsvRecord : caseCsvRecords)

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
