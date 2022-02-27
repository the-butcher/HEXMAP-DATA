package com.igorion.hexmap;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.igorion.app.impl.C19Application;
import com.igorion.http.IHttpRequest;
import com.igorion.http.IHttpResponse;
import com.igorion.http.impl.HttpRequest;
import com.igorion.http.impl.OutboundHttpConfig;
import com.igorion.http.impl.ResponseHandler;
import com.igorion.type.json.impl.JsonTypeImplHexmapDataRoot;
import com.igorion.util.impl.DateUtil;
import com.igorion.util.impl.Storage;

public class HexmapControlParser03VaccinationAgeProvince {

    public static final String URL_TEMPLATE = "https://info.gesundheitsministerium.gv.at/data/archiv/COVID19_vaccination_doses_agegroups_%s.csv";

    public static final SimpleDateFormat DATE_FORMAT_CSV_FILE = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat DATE_FORMAT_____CASE = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static final String FILE___POPULATION = "hexcube-base-population_00_12_14n.csv";

    public static void main(String[] args) throws Exception {

        C19Application.init("");
        C19Application.getInstance().addSubConfig(OutboundHttpConfig.proxy("127.0.0.1", 8888));
        C19Application.getInstance().addSubConfig(OutboundHttpConfig.noopSsl());

        JsonTypeImplHexmapDataRoot dataRoot = new JsonTypeImplHexmapDataRoot("temp.json");

        loadVac2Data();

        // TODO parse by age group

    }

    static void loadVac2Data() throws Exception {

        long minInstant = 1635494400000L; // Date and time (GMT): Friday, 29. October 2021 08:00:00
        long maxInstant = DATE_FORMAT_CSV_FILE.parse(DATE_FORMAT_CSV_FILE.format(new Date())).getTime(); // Date and time (GMT): Sunday, 19. December 2021 08:00:00

        for (long vac2Time = minInstant; vac2Time <= maxInstant; vac2Time += DateUtil.MILLISECONDS_PER__DAY) {

            String datePortion = DATE_FORMAT_CSV_FILE.format(new Date(vac2Time));
            File vac2File = getVac2File(vac2Time);
            if (!vac2File.exists()) {

                String vac2Url = String.format(URL_TEMPLATE, datePortion);
                loadMiscData(vac2Url, vac2File);

                System.out.println(vac2Url + " > " + vac2File);

            }

            // break;

        }

    }

    static File getVac2File(long vac2Time) {
        String datePortion = DATE_FORMAT_CSV_FILE.format(new Date(vac2Time));
        File vac2File = new File(Storage.FOLDER___WORK, "vaccination_age_" + datePortion + ".csv");
        return vac2File;
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

}