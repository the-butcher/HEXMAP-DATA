package com.igorion.hexmap;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igorion.app.impl.C19Application;
import com.igorion.http.IHttpRequest;
import com.igorion.http.IHttpResponse;
import com.igorion.http.impl.HttpRequest;
import com.igorion.http.impl.OutboundHttpConfig;
import com.igorion.http.impl.ResponseHandler;
import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IDataSet;
import com.igorion.report.dataset.IDataSetFactory;
import com.igorion.report.dataset.impl.DataSetFactoryImplCsv;
import com.igorion.report.dataset.impl.DataSetFactoryImplFeature;
import com.igorion.report.value.FieldTypes;
import com.igorion.type.json.impl.JsonTypeImplHexmapDataRoot;
import com.igorion.type.json.impl.JsonTypeImplSalzburgData;
import com.igorion.type.json.impl.JsonTypeImplSalzburgRoot;
import com.igorion.type.json.response.JsonTypeImplQueryResultPoint;

public class HexmapControlParserSalzburgX4Gemeinde {

    public static Map<String, String> KEYSET_MUNICIPALITY = new LinkedHashMap<>();
    static {
        KEYSET_MUNICIPALITY.put("#####", "Österreich");
//        KEYSET_MUNICIPALITY.put("1####", "Burgenland");
//        KEYSET_MUNICIPALITY.put("2####", "Kärnten");
//        KEYSET_MUNICIPALITY.put("3####", "Niederösterreich");
//        KEYSET_MUNICIPALITY.put("4####", "Oberösterreich");
        KEYSET_MUNICIPALITY.put("5####", "Salzburg");
        KEYSET_MUNICIPALITY.put("501##", "Salzburg(Stadt)");
        KEYSET_MUNICIPALITY.put("502##", "Hallein");
        KEYSET_MUNICIPALITY.put("503##", "Salzburg-Umgebung");
        KEYSET_MUNICIPALITY.put("504##", "Sankt Johann im Pongau");
        KEYSET_MUNICIPALITY.put("505##", "Tamsweg");
        KEYSET_MUNICIPALITY.put("506##", "Zell am See");
//        KEYSET_MUNICIPALITY.put("6####", "Steiermark");
//        KEYSET_MUNICIPALITY.put("7####", "Tirol");
//        KEYSET_MUNICIPALITY.put("8####", "Vorarlberg");
//        KEYSET_MUNICIPALITY.put("9####", "Wien");
    }

    public static final Map<String, String> KEYSET_MUNICIPIALITY = new LinkedHashMap<>();

    public static final String URL_TEMPLATE = "https://github.com/fitforfire/covid-sbg/blob/master/data/%s?raw=true";
    public static final long MIN_INSTANT = 1619222880000L; // Date and time (GMT): Saturday, 24. April 2021 00:08:00

    public static final File FOLDER_______BASE = new File("C:\\privat\\_projects_cov\\covid2019_hexmap_data");
    public static final String FILE_TEMPLATE_JSON = "salzburg_gemeinde_%s.json";
    public static final String FILE___POPULATION = "endgueltige_bevoelkerungszahl_fuer_das_finanzjahr_2022_je_gemeinde.csv";

    public static final SimpleDateFormat DATE_FORMAT___GITHUB = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_FORMAT_____FILE = new SimpleDateFormat("yyyyMMdd");
//    public static final SimpleDateFormat DATE_FORMAT_____CASE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'000Z.json'");

    public static final List<String> FILE_NAMES = new ArrayList<>();
    static {
        FILE_NAMES.add("2020-12-17T15:45:46.000Z.json");
        FILE_NAMES.add("2020-12-18T15:45:53.000Z.json");
        FILE_NAMES.add("2020-12-19T15:46:12.000Z.json");
        FILE_NAMES.add("2020-12-20T15:45:55.000Z.json");
        FILE_NAMES.add("2020-12-21T15:45:50.000Z.json");
        FILE_NAMES.add("2020-12-22T15:31:01.000Z.json");
        FILE_NAMES.add("2020-12-23T15:00:59.000Z.json");
        FILE_NAMES.add("2020-12-24T15:46:04.000Z.json");
        FILE_NAMES.add("2020-12-25T15:45:43.000Z.json");
        FILE_NAMES.add("2020-12-26T15:45:54.000Z.json");
        FILE_NAMES.add("2020-12-27T15:45:37.000Z.json");
        FILE_NAMES.add("2020-12-28T15:30:48.000Z.json");
        FILE_NAMES.add("2020-12-29T15:01:15.000Z.json");
        FILE_NAMES.add("2020-12-30T15:30:56.000Z.json");
        FILE_NAMES.add("2020-12-31T15:45:37.000Z.json");
        FILE_NAMES.add("2021-01-01T15:45:44.000Z.json");
        FILE_NAMES.add("2021-01-02T15:45:37.000Z.json");
        FILE_NAMES.add("2021-01-03T15:45:42.000Z.json");
        FILE_NAMES.add("2021-01-04T15:34:50.000Z.json");
        FILE_NAMES.add("2021-01-05T15:15:45.000Z.json");
        FILE_NAMES.add("2021-01-06T15:45:32.000Z.json");
        FILE_NAMES.add("2021-01-07T15:45:50.000Z.json");
        FILE_NAMES.add("2021-01-08T15:45:47.000Z.json");
        FILE_NAMES.add("2021-01-09T15:45:34.000Z.json");
        FILE_NAMES.add("2021-01-10T15:45:49.000Z.json");
        FILE_NAMES.add("2021-01-11T15:46:07.000Z.json");
        FILE_NAMES.add("2021-01-12T15:45:40.000Z.json");
        FILE_NAMES.add("2021-01-13T15:31:42.000Z.json");
        FILE_NAMES.add("2021-01-14T15:46:11.000Z.json");
        FILE_NAMES.add("2021-01-15T15:45:44.000Z.json");
        FILE_NAMES.add("2021-01-16T15:45:50.000Z.json");
        FILE_NAMES.add("2021-01-17T15:45:46.000Z.json");
        FILE_NAMES.add("2021-01-18T15:46:07.000Z.json");
        FILE_NAMES.add("2021-01-19T15:45:53.000Z.json");
        FILE_NAMES.add("2021-01-20T15:05:17.000Z.json");
        FILE_NAMES.add("2021-01-21T15:45:46.000Z.json");
        FILE_NAMES.add("2021-01-22T15:45:47.000Z.json");
        FILE_NAMES.add("2021-01-23T15:45:56.000Z.json");
        FILE_NAMES.add("2021-01-24T15:45:55.000Z.json");
        FILE_NAMES.add("2021-01-25T15:45:54.000Z.json");
        FILE_NAMES.add("2021-01-26T15:46:03.000Z.json");
        FILE_NAMES.add("2021-01-27T15:15:44.000Z.json");
        FILE_NAMES.add("2021-01-28T15:45:38.000Z.json");
        FILE_NAMES.add("2021-01-29T15:45:53.000Z.json");
        FILE_NAMES.add("2021-01-30T15:45:42.000Z.json");
        FILE_NAMES.add("2021-01-31T15:45:53.000Z.json");
        FILE_NAMES.add("2021-02-01T15:45:44.000Z.json");
        FILE_NAMES.add("2021-02-02T15:45:45.000Z.json");
        FILE_NAMES.add("2021-02-03T15:45:46.000Z.json");
        FILE_NAMES.add("2021-02-04T13:45:45.000Z.json");
        FILE_NAMES.add("2021-02-05T15:45:40.000Z.json");
        FILE_NAMES.add("2021-02-06T15:45:42.000Z.json");
        FILE_NAMES.add("2021-02-07T15:45:42.000Z.json");
        FILE_NAMES.add("2021-02-08T15:30:56.000Z.json");
        FILE_NAMES.add("2021-02-09T15:45:52.000Z.json");
        FILE_NAMES.add("2021-02-10T15:05:21.000Z.json");
        FILE_NAMES.add("2021-02-11T15:45:44.000Z.json");
        FILE_NAMES.add("2021-02-12T15:45:45.000Z.json");
        FILE_NAMES.add("2021-02-13T15:45:58.000Z.json");
        FILE_NAMES.add("2021-02-14T15:45:42.000Z.json");
        FILE_NAMES.add("2021-02-15T15:01:11.000Z.json");
        FILE_NAMES.add("2021-02-16T15:45:45.000Z.json");
        FILE_NAMES.add("2021-02-17T14:35:23.000Z.json");
        FILE_NAMES.add("2021-02-18T15:31:03.000Z.json");
        FILE_NAMES.add("2021-02-19T15:46:08.000Z.json");
        FILE_NAMES.add("2021-02-20T15:45:51.000Z.json");
        FILE_NAMES.add("2021-02-21T15:31:21.000Z.json");
        FILE_NAMES.add("2021-02-22T14:31:01.000Z.json");
        FILE_NAMES.add("2021-02-23T15:35:22.000Z.json");
        FILE_NAMES.add("2021-02-24T14:01:27.000Z.json");
        FILE_NAMES.add("2021-02-25T14:46:04.000Z.json");
        FILE_NAMES.add("2021-02-26T15:16:06.000Z.json");
        FILE_NAMES.add("2021-02-27T15:45:56.000Z.json");
        FILE_NAMES.add("2021-02-28T15:34:55.000Z.json");
        FILE_NAMES.add("2021-03-01T13:45:49.000Z.json");
        FILE_NAMES.add("2021-03-02T14:02:15.000Z.json");
        FILE_NAMES.add("2021-03-03T13:01:11.000Z.json");
        FILE_NAMES.add("2021-03-04T13:01:17.000Z.json");
        FILE_NAMES.add("2021-03-05T12:01:43.000Z.json");
        FILE_NAMES.add("2021-03-06T15:01:12.000Z.json");
        FILE_NAMES.add("2021-03-07T15:16:08.000Z.json");
        FILE_NAMES.add("2021-03-08T13:31:09.000Z.json");
        FILE_NAMES.add("2021-03-09T13:31:02.000Z.json");
        FILE_NAMES.add("2021-03-10T14:02:11.000Z.json");
        FILE_NAMES.add("2021-03-11T14:15:53.000Z.json");
        FILE_NAMES.add("2021-03-12T14:45:52.000Z.json");
        FILE_NAMES.add("2021-03-13T15:31:26.000Z.json");
        FILE_NAMES.add("2021-03-14T14:49:39.000Z.json");
        FILE_NAMES.add("2021-03-15T13:31:02.000Z.json");
        FILE_NAMES.add("2021-03-16T14:45:54.000Z.json");
        FILE_NAMES.add("2021-03-17T14:31:08.000Z.json");
        FILE_NAMES.add("2021-03-18T13:45:41.000Z.json");
        FILE_NAMES.add("2021-03-19T15:01:30.000Z.json");
        FILE_NAMES.add("2021-03-20T15:30:57.000Z.json");
        FILE_NAMES.add("2021-03-21T15:30:56.000Z.json");
        FILE_NAMES.add("2021-03-22T14:15:53.000Z.json");
        FILE_NAMES.add("2021-03-23T14:45:53.000Z.json");
        FILE_NAMES.add("2021-03-24T14:45:45.000Z.json");
        FILE_NAMES.add("2021-03-25T14:31:08.000Z.json");
        FILE_NAMES.add("2021-03-26T14:45:44.000Z.json");
        FILE_NAMES.add("2021-03-27T14:15:43.000Z.json");
        FILE_NAMES.add("2021-03-28T15:45:38.000Z.json");
        FILE_NAMES.add("2021-03-29T14:01:15.000Z.json");
        FILE_NAMES.add("2021-03-30T15:45:48.000Z.json");
        FILE_NAMES.add("2021-03-31T14:45:47.000Z.json");
        FILE_NAMES.add("2021-04-01T14:15:46.000Z.json");
        FILE_NAMES.add("2021-04-02T14:31:09.000Z.json");
        FILE_NAMES.add("2021-04-03T15:01:20.000Z.json");
        FILE_NAMES.add("2021-04-04T14:30:52.000Z.json");
        FILE_NAMES.add("2021-04-05T14:01:08.000Z.json");
        FILE_NAMES.add("2021-04-06T14:45:48.000Z.json");
        FILE_NAMES.add("2021-04-07T13:31:14.000Z.json");
        FILE_NAMES.add("2021-04-08T15:45:48.000Z.json");
        FILE_NAMES.add("2021-04-09T15:45:40.000Z.json");
        FILE_NAMES.add("2021-04-10T15:45:44.000Z.json");
        FILE_NAMES.add("2021-04-11T15:45:40.000Z.json");
        FILE_NAMES.add("2021-04-12T15:45:50.000Z.json");
        FILE_NAMES.add("2021-04-13T15:45:45.000Z.json");
        FILE_NAMES.add("2021-04-14T15:45:48.000Z.json");
        FILE_NAMES.add("2021-04-15T15:45:47.000Z.json");
        FILE_NAMES.add("2021-04-16T15:47:56.000Z.json");
        FILE_NAMES.add("2021-04-17T15:45:40.000Z.json");
        FILE_NAMES.add("2021-04-18T15:45:42.000Z.json");
        FILE_NAMES.add("2021-04-19T15:45:42.000Z.json");
        FILE_NAMES.add("2021-04-20T15:15:52.000Z.json");
        FILE_NAMES.add("2021-04-21T15:45:49.000Z.json");
        FILE_NAMES.add("2021-04-22T15:31:12.000Z.json");
        FILE_NAMES.add("2021-04-23T15:02:59.000Z.json");
        FILE_NAMES.add("2021-04-24T06:30:54.000Z.json");
        FILE_NAMES.add("2021-04-25T06:31:10.000Z.json");
        FILE_NAMES.add("2021-04-25T15:45:42.000Z.json");
        FILE_NAMES.add("2021-04-26T06:30:59.000Z.json");
        FILE_NAMES.add("2021-04-26T15:01:14.000Z.json");
        FILE_NAMES.add("2021-04-27T06:31:22.000Z.json");
        FILE_NAMES.add("2021-04-27T15:31:14.000Z.json");
        FILE_NAMES.add("2021-04-28T06:32:37.000Z.json");
        FILE_NAMES.add("2021-04-28T15:01:37.000Z.json");
        FILE_NAMES.add("2021-04-29T06:30:59.000Z.json");
        FILE_NAMES.add("2021-04-29T15:16:08.000Z.json");
        FILE_NAMES.add("2021-04-30T05:45:52.000Z.json");
        FILE_NAMES.add("2021-04-30T15:15:53.000Z.json");
        FILE_NAMES.add("2021-05-01T06:31:02.000Z.json");
        FILE_NAMES.add("2021-05-01T15:31:05.000Z.json");
        FILE_NAMES.add("2021-05-02T06:31:04.000Z.json");
        FILE_NAMES.add("2021-05-02T15:45:48.000Z.json");
        FILE_NAMES.add("2021-05-03T06:30:58.000Z.json");
        FILE_NAMES.add("2021-05-03T15:15:48.000Z.json");
        FILE_NAMES.add("2021-05-04T06:30:58.000Z.json");
        FILE_NAMES.add("2021-05-04T15:45:49.000Z.json");
        FILE_NAMES.add("2021-05-05T06:31:15.000Z.json");
        FILE_NAMES.add("2021-05-05T15:31:02.000Z.json");
        FILE_NAMES.add("2021-05-06T06:30:56.000Z.json");
        FILE_NAMES.add("2021-05-06T15:45:48.000Z.json");
        FILE_NAMES.add("2021-05-07T06:31:19.000Z.json");
        FILE_NAMES.add("2021-05-07T15:45:45.000Z.json");
        FILE_NAMES.add("2021-05-08T06:31:09.000Z.json");
        FILE_NAMES.add("2021-05-08T15:45:37.000Z.json");
        FILE_NAMES.add("2021-05-09T06:31:29.000Z.json");
        FILE_NAMES.add("2021-05-09T15:46:12.000Z.json");
        FILE_NAMES.add("2021-05-10T06:31:42.000Z.json");
        FILE_NAMES.add("2021-05-11T06:30:54.000Z.json");
        FILE_NAMES.add("2021-05-11T15:31:29.000Z.json");
        FILE_NAMES.add("2021-05-12T06:30:53.000Z.json");
        FILE_NAMES.add("2021-05-12T15:31:13.000Z.json");
        FILE_NAMES.add("2021-05-13T06:30:57.000Z.json");
        FILE_NAMES.add("2021-05-13T14:15:41.000Z.json");
        FILE_NAMES.add("2021-05-14T06:31:01.000Z.json");
        FILE_NAMES.add("2021-05-14T15:45:41.000Z.json");
        FILE_NAMES.add("2021-05-15T06:31:10.000Z.json");
        FILE_NAMES.add("2021-05-15T15:45:42.000Z.json");
        FILE_NAMES.add("2021-05-16T15:45:55.000Z.json");
        FILE_NAMES.add("2021-05-17T06:30:51.000Z.json");
        FILE_NAMES.add("2021-05-17T15:45:43.000Z.json");
        FILE_NAMES.add("2021-05-18T06:31:03.000Z.json");
        FILE_NAMES.add("2021-05-18T15:45:46.000Z.json");
        FILE_NAMES.add("2021-05-19T06:30:59.000Z.json");
        FILE_NAMES.add("2021-05-19T15:45:46.000Z.json");
        FILE_NAMES.add("2021-05-20T06:30:57.000Z.json");
        FILE_NAMES.add("2021-05-20T15:45:41.000Z.json");
        FILE_NAMES.add("2021-05-21T06:31:01.000Z.json");
        FILE_NAMES.add("2021-05-21T15:45:41.000Z.json");
        FILE_NAMES.add("2021-05-22T06:30:56.000Z.json");
        FILE_NAMES.add("2021-05-22T15:45:43.000Z.json");
        FILE_NAMES.add("2021-05-23T06:30:50.000Z.json");
        FILE_NAMES.add("2021-05-23T15:45:40.000Z.json");
        FILE_NAMES.add("2021-05-24T06:31:02.000Z.json");
        FILE_NAMES.add("2021-05-24T15:45:44.000Z.json");
        FILE_NAMES.add("2021-05-25T06:31:14.000Z.json");
        FILE_NAMES.add("2021-05-25T15:31:07.000Z.json");
        FILE_NAMES.add("2021-05-26T06:30:55.000Z.json");
        FILE_NAMES.add("2021-05-26T15:45:55.000Z.json");
        FILE_NAMES.add("2021-05-27T06:31:41.000Z.json");
        FILE_NAMES.add("2021-05-27T15:46:22.000Z.json");
        FILE_NAMES.add("2021-05-28T06:31:31.000Z.json");
        FILE_NAMES.add("2021-05-28T15:46:24.000Z.json");
        FILE_NAMES.add("2021-05-29T04:46:21.000Z.json");
        FILE_NAMES.add("2021-05-29T15:45:53.000Z.json");
        FILE_NAMES.add("2021-05-30T06:35:20.000Z.json");
        FILE_NAMES.add("2021-05-30T15:45:56.000Z.json");
        FILE_NAMES.add("2021-05-31T06:31:06.000Z.json");
        FILE_NAMES.add("2021-05-31T15:45:52.000Z.json");
        FILE_NAMES.add("2021-06-01T06:31:12.000Z.json");
        FILE_NAMES.add("2021-06-01T15:45:57.000Z.json");
        FILE_NAMES.add("2021-06-02T06:31:15.000Z.json");
        FILE_NAMES.add("2021-06-02T15:45:55.000Z.json");
        FILE_NAMES.add("2021-06-03T06:31:03.000Z.json");
        FILE_NAMES.add("2021-06-03T15:45:56.000Z.json");
        FILE_NAMES.add("2021-06-04T06:31:44.000Z.json");
        FILE_NAMES.add("2021-06-04T15:45:55.000Z.json");
        FILE_NAMES.add("2021-06-05T06:31:10.000Z.json");
        FILE_NAMES.add("2021-06-05T15:45:53.000Z.json");
        FILE_NAMES.add("2021-06-06T06:31:06.000Z.json");
        FILE_NAMES.add("2021-06-06T15:45:49.000Z.json");
        FILE_NAMES.add("2021-06-07T06:31:02.000Z.json");
        FILE_NAMES.add("2021-06-07T15:45:57.000Z.json");
        FILE_NAMES.add("2021-06-08T06:31:17.000Z.json");
        FILE_NAMES.add("2021-06-08T15:45:53.000Z.json");
        FILE_NAMES.add("2021-06-09T06:31:33.000Z.json");
        FILE_NAMES.add("2021-06-09T15:45:51.000Z.json");
        FILE_NAMES.add("2021-06-10T06:31:08.000Z.json");
        FILE_NAMES.add("2021-06-10T15:50:59.000Z.json");
        FILE_NAMES.add("2021-06-11T06:31:31.000Z.json");
        FILE_NAMES.add("2021-06-11T15:46:24.000Z.json");
        FILE_NAMES.add("2021-06-12T06:31:13.000Z.json");
        FILE_NAMES.add("2021-06-12T15:46:06.000Z.json");
        FILE_NAMES.add("2021-06-13T06:30:55.000Z.json");
        FILE_NAMES.add("2021-06-13T15:45:53.000Z.json");
        FILE_NAMES.add("2021-06-14T06:31:18.000Z.json");
        FILE_NAMES.add("2021-06-14T15:45:58.000Z.json");
        FILE_NAMES.add("2021-06-15T06:31:11.000Z.json");
        FILE_NAMES.add("2021-06-15T15:46:00.000Z.json");
        FILE_NAMES.add("2021-06-16T06:31:02.000Z.json");
        FILE_NAMES.add("2021-06-16T15:45:56.000Z.json");
        FILE_NAMES.add("2021-06-17T06:31:12.000Z.json");
        FILE_NAMES.add("2021-06-17T16:31:17.000Z.json");
        FILE_NAMES.add("2021-06-18T06:31:17.000Z.json");
        FILE_NAMES.add("2021-06-18T15:45:59.000Z.json");
        FILE_NAMES.add("2021-06-19T06:31:14.000Z.json");
        FILE_NAMES.add("2021-06-19T15:45:56.000Z.json");
        FILE_NAMES.add("2021-06-20T06:31:27.000Z.json");
        FILE_NAMES.add("2021-06-20T15:46:15.000Z.json");
        FILE_NAMES.add("2021-06-21T06:31:32.000Z.json");
        FILE_NAMES.add("2021-06-21T15:46:27.000Z.json");
        FILE_NAMES.add("2021-06-22T06:31:33.000Z.json");
        FILE_NAMES.add("2021-06-22T15:46:26.000Z.json");
        FILE_NAMES.add("2021-06-23T06:31:20.000Z.json");
        FILE_NAMES.add("2021-06-23T15:46:03.000Z.json");
        FILE_NAMES.add("2021-06-24T06:31:09.000Z.json");
        FILE_NAMES.add("2021-06-24T15:46:02.000Z.json");
        FILE_NAMES.add("2021-06-25T06:31:12.000Z.json");
        FILE_NAMES.add("2021-06-25T15:45:57.000Z.json");
        FILE_NAMES.add("2021-06-26T06:31:24.000Z.json");
        FILE_NAMES.add("2021-06-26T15:46:11.000Z.json");
        FILE_NAMES.add("2021-06-27T06:31:44.000Z.json");
        FILE_NAMES.add("2021-06-27T15:46:11.000Z.json");
        FILE_NAMES.add("2021-06-28T06:31:25.000Z.json");
        FILE_NAMES.add("2021-06-28T15:46:23.000Z.json");
        FILE_NAMES.add("2021-06-29T06:31:24.000Z.json");
        FILE_NAMES.add("2021-06-29T15:46:19.000Z.json");
        FILE_NAMES.add("2021-06-30T06:31:56.000Z.json");
        FILE_NAMES.add("2021-06-30T15:46:24.000Z.json");
        FILE_NAMES.add("2021-07-01T06:31:47.000Z.json");
        FILE_NAMES.add("2021-07-01T15:46:27.000Z.json");
        FILE_NAMES.add("2021-07-02T06:31:53.000Z.json");
        FILE_NAMES.add("2021-07-02T15:46:26.000Z.json");
        FILE_NAMES.add("2021-07-03T06:31:57.000Z.json");
        FILE_NAMES.add("2021-07-03T15:46:14.000Z.json");
        FILE_NAMES.add("2021-07-04T06:32:11.000Z.json");
        FILE_NAMES.add("2021-07-04T15:46:33.000Z.json");
        FILE_NAMES.add("2021-07-05T06:31:59.000Z.json");
        FILE_NAMES.add("2021-07-05T15:46:21.000Z.json");
        FILE_NAMES.add("2021-07-06T06:32:00.000Z.json");
        FILE_NAMES.add("2021-07-06T15:46:56.000Z.json");
        FILE_NAMES.add("2021-07-07T06:31:34.000Z.json");
        FILE_NAMES.add("2021-07-07T15:46:22.000Z.json");
        FILE_NAMES.add("2021-07-08T06:31:57.000Z.json");
        FILE_NAMES.add("2021-07-08T15:46:23.000Z.json");
        FILE_NAMES.add("2021-07-09T06:31:47.000Z.json");
        FILE_NAMES.add("2021-07-09T15:46:15.000Z.json");
        FILE_NAMES.add("2021-07-10T06:31:36.000Z.json");
        FILE_NAMES.add("2021-07-10T15:46:15.000Z.json");
        FILE_NAMES.add("2021-07-11T06:31:37.000Z.json");
        FILE_NAMES.add("2021-07-11T15:46:20.000Z.json");
        FILE_NAMES.add("2021-07-12T06:31:44.000Z.json");
        FILE_NAMES.add("2021-07-12T15:46:19.000Z.json");
        FILE_NAMES.add("2021-07-13T06:31:31.000Z.json");
        FILE_NAMES.add("2021-07-13T15:46:33.000Z.json");
        FILE_NAMES.add("2021-07-14T06:31:39.000Z.json");
        FILE_NAMES.add("2021-07-14T15:46:25.000Z.json");
        FILE_NAMES.add("2021-07-15T06:31:34.000Z.json");
        FILE_NAMES.add("2021-07-15T15:46:15.000Z.json");
        FILE_NAMES.add("2021-07-16T06:31:35.000Z.json");
        FILE_NAMES.add("2021-07-16T15:46:22.000Z.json");
        FILE_NAMES.add("2021-07-17T06:31:46.000Z.json");
        FILE_NAMES.add("2021-07-17T15:46:15.000Z.json");
        FILE_NAMES.add("2021-07-18T06:31:20.000Z.json");
        FILE_NAMES.add("2021-07-18T15:46:12.000Z.json");
        FILE_NAMES.add("2021-07-19T06:31:29.000Z.json");
        FILE_NAMES.add("2021-07-19T15:46:17.000Z.json");
        FILE_NAMES.add("2021-07-20T06:35:03.000Z.json");
        FILE_NAMES.add("2021-07-20T15:50:47.000Z.json");
        FILE_NAMES.add("2021-07-21T06:32:21.000Z.json");
        FILE_NAMES.add("2021-07-21T15:46:24.000Z.json");
        FILE_NAMES.add("2021-07-22T06:31:34.000Z.json");
        FILE_NAMES.add("2021-07-22T15:46:19.000Z.json");
        FILE_NAMES.add("2021-07-23T06:31:32.000Z.json");
        FILE_NAMES.add("2021-07-23T15:46:28.000Z.json");
        FILE_NAMES.add("2021-07-24T06:31:32.000Z.json");
        FILE_NAMES.add("2021-07-24T15:46:20.000Z.json");
        FILE_NAMES.add("2021-07-25T06:31:43.000Z.json");
        FILE_NAMES.add("2021-07-25T15:46:17.000Z.json");
        FILE_NAMES.add("2021-07-26T06:31:37.000Z.json");
        FILE_NAMES.add("2021-07-26T15:46:25.000Z.json");
        FILE_NAMES.add("2021-07-27T06:31:51.000Z.json");
        FILE_NAMES.add("2021-07-27T15:46:22.000Z.json");
        FILE_NAMES.add("2021-07-28T06:31:35.000Z.json");
        FILE_NAMES.add("2021-07-28T15:46:23.000Z.json");
        FILE_NAMES.add("2021-07-29T06:31:38.000Z.json");
        FILE_NAMES.add("2021-07-29T15:46:23.000Z.json");
        FILE_NAMES.add("2021-07-30T06:31:32.000Z.json");
        FILE_NAMES.add("2021-07-30T15:46:20.000Z.json");
        FILE_NAMES.add("2021-07-31T06:31:43.000Z.json");
        FILE_NAMES.add("2021-07-31T15:46:27.000Z.json");
        FILE_NAMES.add("2021-08-01T06:31:46.000Z.json");
        FILE_NAMES.add("2021-08-01T15:46:21.000Z.json");
        FILE_NAMES.add("2021-08-02T06:31:41.000Z.json");
        FILE_NAMES.add("2021-08-02T15:46:20.000Z.json");
        FILE_NAMES.add("2021-08-03T06:31:54.000Z.json");
        FILE_NAMES.add("2021-08-03T15:46:21.000Z.json");
        FILE_NAMES.add("2021-08-04T06:31:51.000Z.json");
        FILE_NAMES.add("2021-08-04T15:46:16.000Z.json");
        FILE_NAMES.add("2021-08-05T06:31:30.000Z.json");
        FILE_NAMES.add("2021-08-05T15:46:25.000Z.json");
        FILE_NAMES.add("2021-08-06T06:31:21.000Z.json");
        FILE_NAMES.add("2021-08-06T15:46:17.000Z.json");
        FILE_NAMES.add("2021-08-07T06:31:34.000Z.json");
        FILE_NAMES.add("2021-08-07T15:46:19.000Z.json");
        FILE_NAMES.add("2021-08-08T06:31:33.000Z.json");
        FILE_NAMES.add("2021-08-08T15:46:18.000Z.json");
        FILE_NAMES.add("2021-08-09T06:31:43.000Z.json");
        FILE_NAMES.add("2021-08-09T15:46:21.000Z.json");
        FILE_NAMES.add("2021-08-10T06:31:42.000Z.json");
        FILE_NAMES.add("2021-08-10T15:46:36.000Z.json");
        FILE_NAMES.add("2021-08-11T06:31:44.000Z.json");
        FILE_NAMES.add("2021-08-11T15:46:23.000Z.json");
        FILE_NAMES.add("2021-08-12T06:31:37.000Z.json");
        FILE_NAMES.add("2021-08-12T15:46:23.000Z.json");
        FILE_NAMES.add("2021-08-13T06:31:50.000Z.json");
        FILE_NAMES.add("2021-08-13T15:46:26.000Z.json");
        FILE_NAMES.add("2021-08-14T06:31:42.000Z.json");
        FILE_NAMES.add("2021-08-14T15:46:24.000Z.json");
        FILE_NAMES.add("2021-08-15T06:31:32.000Z.json");
        FILE_NAMES.add("2021-08-15T15:46:19.000Z.json");
        FILE_NAMES.add("2021-08-16T06:31:34.000Z.json");
        FILE_NAMES.add("2021-08-16T15:46:21.000Z.json");
        FILE_NAMES.add("2021-08-17T06:31:37.000Z.json");
        FILE_NAMES.add("2021-08-17T15:46:27.000Z.json");
        FILE_NAMES.add("2021-08-18T06:31:39.000Z.json");
        FILE_NAMES.add("2021-08-18T15:46:23.000Z.json");
        FILE_NAMES.add("2021-08-19T06:31:49.000Z.json");
        FILE_NAMES.add("2021-08-19T15:46:24.000Z.json");
        FILE_NAMES.add("2021-08-20T06:31:45.000Z.json");
        FILE_NAMES.add("2021-08-20T15:46:20.000Z.json");
        FILE_NAMES.add("2021-08-21T06:31:41.000Z.json");
        FILE_NAMES.add("2021-08-21T15:46:17.000Z.json");
        FILE_NAMES.add("2021-08-22T06:31:52.000Z.json");
        FILE_NAMES.add("2021-08-22T15:46:17.000Z.json");
        FILE_NAMES.add("2021-08-23T06:31:40.000Z.json");
        FILE_NAMES.add("2021-08-23T15:46:29.000Z.json");
        FILE_NAMES.add("2021-08-24T06:31:59.000Z.json");
        FILE_NAMES.add("2021-08-24T15:46:31.000Z.json");
        FILE_NAMES.add("2021-08-25T06:32:05.000Z.json");
        FILE_NAMES.add("2021-08-25T15:46:42.000Z.json");
        FILE_NAMES.add("2021-08-26T06:31:58.000Z.json");
        FILE_NAMES.add("2021-08-26T15:46:50.000Z.json");
        FILE_NAMES.add("2021-08-27T06:31:37.000Z.json");
        FILE_NAMES.add("2021-08-27T15:46:24.000Z.json");
        FILE_NAMES.add("2021-08-28T06:31:38.000Z.json");
        FILE_NAMES.add("2021-08-28T15:46:19.000Z.json");
        FILE_NAMES.add("2021-08-29T06:31:37.000Z.json");
        FILE_NAMES.add("2021-08-29T15:46:20.000Z.json");
        FILE_NAMES.add("2021-08-30T06:31:52.000Z.json");
        FILE_NAMES.add("2021-08-30T15:46:26.000Z.json");
        FILE_NAMES.add("2021-08-31T06:31:43.000Z.json");
        FILE_NAMES.add("2021-08-31T15:46:23.000Z.json");
        FILE_NAMES.add("2021-09-01T06:31:43.000Z.json");
        FILE_NAMES.add("2021-09-01T15:46:27.000Z.json");
        FILE_NAMES.add("2021-09-02T06:31:51.000Z.json");
        FILE_NAMES.add("2021-09-02T15:46:29.000Z.json");
        FILE_NAMES.add("2021-09-03T06:31:47.000Z.json");
        FILE_NAMES.add("2021-09-03T15:46:24.000Z.json");
        FILE_NAMES.add("2021-09-04T06:31:40.000Z.json");
        FILE_NAMES.add("2021-09-04T15:46:23.000Z.json");
        FILE_NAMES.add("2021-09-05T06:31:38.000Z.json");
        FILE_NAMES.add("2021-09-05T15:46:23.000Z.json");
        FILE_NAMES.add("2021-09-06T06:31:37.000Z.json");
        FILE_NAMES.add("2021-09-06T15:46:31.000Z.json");
        FILE_NAMES.add("2021-09-07T15:46:39.000Z.json");
        FILE_NAMES.add("2021-09-08T06:31:42.000Z.json");
        FILE_NAMES.add("2021-09-08T15:46:24.000Z.json");
        FILE_NAMES.add("2021-09-09T06:31:39.000Z.json");
        FILE_NAMES.add("2021-09-09T15:46:31.000Z.json");
        FILE_NAMES.add("2021-09-10T06:31:55.000Z.json");
        FILE_NAMES.add("2021-09-10T15:46:20.000Z.json");
        FILE_NAMES.add("2021-09-11T06:31:44.000Z.json");
        FILE_NAMES.add("2021-09-11T15:46:28.000Z.json");
        FILE_NAMES.add("2021-09-12T06:32:20.000Z.json");
        FILE_NAMES.add("2021-09-12T15:50:33.000Z.json");
        FILE_NAMES.add("2021-09-13T06:31:37.000Z.json");
        FILE_NAMES.add("2021-09-13T15:46:21.000Z.json");
        FILE_NAMES.add("2021-09-14T06:31:35.000Z.json");
        FILE_NAMES.add("2021-09-14T15:46:32.000Z.json");
        FILE_NAMES.add("2021-09-15T06:31:47.000Z.json");
        FILE_NAMES.add("2021-09-15T15:46:28.000Z.json");
        FILE_NAMES.add("2021-09-16T06:31:39.000Z.json");
        FILE_NAMES.add("2021-09-16T15:46:34.000Z.json");
        FILE_NAMES.add("2021-09-17T06:31:57.000Z.json");
        FILE_NAMES.add("2021-09-17T14:59:42.000Z.json");
        FILE_NAMES.add("2021-09-18T06:31:56.000Z.json");
        FILE_NAMES.add("2021-09-18T15:31:45.000Z.json");
        FILE_NAMES.add("2021-09-19T06:31:25.000Z.json");
        FILE_NAMES.add("2021-09-19T15:31:48.000Z.json");
        FILE_NAMES.add("2021-09-20T06:31:29.000Z.json");
        FILE_NAMES.add("2021-09-20T15:31:51.000Z.json");
        FILE_NAMES.add("2021-09-21T06:31:43.000Z.json");
        FILE_NAMES.add("2021-09-21T15:31:47.000Z.json");
        FILE_NAMES.add("2021-09-22T06:31:50.000Z.json");
        FILE_NAMES.add("2021-09-22T15:31:43.000Z.json");
        FILE_NAMES.add("2021-09-23T06:31:53.000Z.json");
        FILE_NAMES.add("2021-09-23T15:31:57.000Z.json");
        FILE_NAMES.add("2021-09-24T06:32:05.000Z.json");
        FILE_NAMES.add("2021-09-24T15:32:04.000Z.json");
        FILE_NAMES.add("2021-09-25T06:31:55.000Z.json");
        FILE_NAMES.add("2021-09-25T15:31:52.000Z.json");
        FILE_NAMES.add("2021-09-26T06:31:41.000Z.json");
        FILE_NAMES.add("2021-09-26T15:31:50.000Z.json");
        FILE_NAMES.add("2021-09-27T06:31:52.000Z.json");
        FILE_NAMES.add("2021-09-27T15:31:33.000Z.json");
        FILE_NAMES.add("2021-09-28T06:31:36.000Z.json");
        FILE_NAMES.add("2021-09-28T15:31:27.000Z.json");
        FILE_NAMES.add("2021-09-29T06:31:40.000Z.json");
        FILE_NAMES.add("2021-09-29T15:31:22.000Z.json");
        FILE_NAMES.add("2021-09-30T06:31:34.000Z.json");
        FILE_NAMES.add("2021-09-30T15:31:54.000Z.json");
        FILE_NAMES.add("2021-10-01T06:31:22.000Z.json");
        FILE_NAMES.add("2021-10-01T15:31:21.000Z.json");
        FILE_NAMES.add("2021-10-02T06:31:21.000Z.json");
        FILE_NAMES.add("2021-10-02T15:31:26.000Z.json");
        FILE_NAMES.add("2021-10-03T06:31:12.000Z.json");
        FILE_NAMES.add("2021-10-03T15:31:19.000Z.json");
        FILE_NAMES.add("2021-10-04T06:31:31.000Z.json");
        FILE_NAMES.add("2021-10-04T15:31:24.000Z.json");
        FILE_NAMES.add("2021-10-05T06:31:21.000Z.json");
        FILE_NAMES.add("2021-10-05T15:31:32.000Z.json");
        FILE_NAMES.add("2021-10-06T06:31:25.000Z.json");
        FILE_NAMES.add("2021-10-06T15:31:18.000Z.json");
        FILE_NAMES.add("2021-10-07T06:31:42.000Z.json");
        FILE_NAMES.add("2021-10-07T15:31:33.000Z.json");
        FILE_NAMES.add("2021-10-08T06:31:31.000Z.json");
        FILE_NAMES.add("2021-10-08T15:31:30.000Z.json");
        FILE_NAMES.add("2021-10-09T06:31:15.000Z.json");
        FILE_NAMES.add("2021-10-09T15:31:31.000Z.json");
        FILE_NAMES.add("2021-10-10T06:31:11.000Z.json");
        FILE_NAMES.add("2021-10-10T15:31:11.000Z.json");
        FILE_NAMES.add("2021-10-11T06:31:23.000Z.json");
        FILE_NAMES.add("2021-10-11T15:31:27.000Z.json");
        FILE_NAMES.add("2021-10-12T06:31:24.000Z.json");
        FILE_NAMES.add("2021-10-12T15:31:23.000Z.json");
        FILE_NAMES.add("2021-10-13T06:31:25.000Z.json");
        FILE_NAMES.add("2021-10-13T15:31:30.000Z.json");
        FILE_NAMES.add("2021-10-14T06:31:21.000Z.json");
        FILE_NAMES.add("2021-10-14T15:31:40.000Z.json");
        FILE_NAMES.add("2021-10-15T06:31:32.000Z.json");
        FILE_NAMES.add("2021-10-15T15:31:38.000Z.json");
        FILE_NAMES.add("2021-10-16T06:31:26.000Z.json");
        FILE_NAMES.add("2021-10-16T15:31:23.000Z.json");
        FILE_NAMES.add("2021-10-17T06:31:21.000Z.json");
        FILE_NAMES.add("2021-10-17T15:31:19.000Z.json");
        FILE_NAMES.add("2021-10-18T06:31:32.000Z.json");
        FILE_NAMES.add("2021-10-18T15:31:23.000Z.json");
        FILE_NAMES.add("2021-10-19T06:31:50.000Z.json");
        FILE_NAMES.add("2021-10-19T15:31:25.000Z.json");
        FILE_NAMES.add("2021-10-20T06:31:32.000Z.json");
        FILE_NAMES.add("2021-10-21T15:31:52.000Z.json");
        FILE_NAMES.add("2021-10-22T15:31:24.000Z.json");
        FILE_NAMES.add("2021-10-23T06:31:24.000Z.json");
        FILE_NAMES.add("2021-10-23T15:31:25.000Z.json");
        FILE_NAMES.add("2021-10-24T06:31:17.000Z.json");
        FILE_NAMES.add("2021-10-24T15:31:16.000Z.json");
        FILE_NAMES.add("2021-10-25T06:31:19.000Z.json");
        FILE_NAMES.add("2021-10-26T06:31:28.000Z.json");
        FILE_NAMES.add("2021-10-26T15:31:38.000Z.json");
        FILE_NAMES.add("2021-10-27T15:32:02.000Z.json");
        FILE_NAMES.add("2021-10-28T06:31:21.000Z.json");
        FILE_NAMES.add("2021-10-28T15:31:19.000Z.json");
        FILE_NAMES.add("2021-10-29T06:31:34.000Z.json");
        FILE_NAMES.add("2021-10-29T15:31:19.000Z.json");
        FILE_NAMES.add("2021-10-30T06:31:37.000Z.json");
        FILE_NAMES.add("2021-10-30T15:31:33.000Z.json");
        FILE_NAMES.add("2021-10-31T05:32:37.000Z.json");
        FILE_NAMES.add("2021-10-31T14:32:09.000Z.json");
        FILE_NAMES.add("2021-11-01T06:31:15.000Z.json");
        FILE_NAMES.add("2021-11-01T15:31:22.000Z.json");
        FILE_NAMES.add("2021-11-02T06:31:30.000Z.json");
        FILE_NAMES.add("2021-11-02T15:31:26.000Z.json");
        FILE_NAMES.add("2021-11-03T06:31:36.000Z.json");
        FILE_NAMES.add("2021-11-03T15:31:21.000Z.json");
        FILE_NAMES.add("2021-11-04T06:31:14.000Z.json");
        FILE_NAMES.add("2021-11-04T15:31:13.000Z.json");
        FILE_NAMES.add("2021-11-05T06:31:14.000Z.json");
        FILE_NAMES.add("2021-11-05T15:31:14.000Z.json");
        FILE_NAMES.add("2021-11-06T06:31:05.000Z.json");
        FILE_NAMES.add("2021-11-06T15:31:15.000Z.json");
        FILE_NAMES.add("2021-11-07T06:31:15.000Z.json");
        FILE_NAMES.add("2021-11-07T15:31:12.000Z.json");
        FILE_NAMES.add("2021-11-08T06:31:09.000Z.json");
        FILE_NAMES.add("2021-11-08T15:31:12.000Z.json");
        FILE_NAMES.add("2021-11-09T06:31:24.000Z.json");
        FILE_NAMES.add("2021-11-09T15:31:24.000Z.json");
        FILE_NAMES.add("2021-11-10T06:31:09.000Z.json");
        FILE_NAMES.add("2021-11-10T15:31:21.000Z.json");
        FILE_NAMES.add("2021-11-11T06:31:14.000Z.json");
        FILE_NAMES.add("2021-11-11T15:31:16.000Z.json");
        FILE_NAMES.add("2021-11-12T06:31:20.000Z.json");
        FILE_NAMES.add("2021-11-12T15:31:23.000Z.json");
        FILE_NAMES.add("2021-11-13T06:31:08.000Z.json");
        FILE_NAMES.add("2021-11-13T15:31:22.000Z.json");
        FILE_NAMES.add("2021-11-14T06:31:05.000Z.json");
        FILE_NAMES.add("2021-11-14T15:31:06.000Z.json");
        FILE_NAMES.add("2021-11-15T06:31:14.000Z.json");
        FILE_NAMES.add("2021-11-15T15:31:15.000Z.json");
        FILE_NAMES.add("2021-11-16T06:31:11.000Z.json");
        FILE_NAMES.add("2021-11-16T15:31:22.000Z.json");
        FILE_NAMES.add("2021-11-17T06:31:42.000Z.json");
        FILE_NAMES.add("2021-11-17T15:31:18.000Z.json");
        FILE_NAMES.add("2021-11-18T06:31:20.000Z.json");
        FILE_NAMES.add("2021-11-18T15:31:29.000Z.json");
        FILE_NAMES.add("2021-11-19T05:32:29.000Z.json");
        FILE_NAMES.add("2021-11-19T15:31:21.000Z.json");
        FILE_NAMES.add("2021-11-20T06:31:16.000Z.json");
        FILE_NAMES.add("2021-11-20T15:31:16.000Z.json");
        FILE_NAMES.add("2021-11-21T06:31:13.000Z.json");
        FILE_NAMES.add("2021-11-21T15:31:11.000Z.json");
        FILE_NAMES.add("2021-11-22T06:31:41.000Z.json");
        FILE_NAMES.add("2021-11-22T15:31:24.000Z.json");
        FILE_NAMES.add("2021-11-23T06:31:07.000Z.json");
        FILE_NAMES.add("2021-11-23T15:31:16.000Z.json");
        FILE_NAMES.add("2021-11-24T06:31:28.000Z.json");
        FILE_NAMES.add("2021-11-24T15:31:21.000Z.json");
        FILE_NAMES.add("2021-11-25T06:31:17.000Z.json");
        FILE_NAMES.add("2021-11-25T15:31:19.000Z.json");
        FILE_NAMES.add("2021-11-26T06:31:26.000Z.json");
        FILE_NAMES.add("2021-11-26T15:31:21.000Z.json");
        FILE_NAMES.add("2021-11-27T06:31:05.000Z.json");
        FILE_NAMES.add("2021-11-27T15:31:00.000Z.json");
        FILE_NAMES.add("2021-11-28T06:31:24.000Z.json");
        FILE_NAMES.add("2021-11-28T15:31:26.000Z.json");
        FILE_NAMES.add("2021-11-29T06:31:35.000Z.json");
        FILE_NAMES.add("2021-11-29T15:31:24.000Z.json");
        FILE_NAMES.add("2021-11-30T06:31:17.000Z.json");
        FILE_NAMES.add("2021-11-30T15:31:24.000Z.json");
        FILE_NAMES.add("2021-12-01T06:31:39.000Z.json");
        FILE_NAMES.add("2021-12-01T15:31:25.000Z.json");
        FILE_NAMES.add("2021-12-02T06:31:25.000Z.json");
        FILE_NAMES.add("2021-12-02T15:31:20.000Z.json");
        FILE_NAMES.add("2021-12-03T06:31:23.000Z.json");
        FILE_NAMES.add("2021-12-03T15:31:29.000Z.json");
        FILE_NAMES.add("2021-12-04T06:31:48.000Z.json");
        FILE_NAMES.add("2021-12-04T15:31:22.000Z.json");
        FILE_NAMES.add("2021-12-05T06:31:02.000Z.json");
        FILE_NAMES.add("2021-12-05T15:31:24.000Z.json");
        FILE_NAMES.add("2021-12-06T06:31:24.000Z.json");
        FILE_NAMES.add("2021-12-06T15:31:33.000Z.json");
        FILE_NAMES.add("2021-12-07T06:31:22.000Z.json");
        FILE_NAMES.add("2021-12-07T15:31:26.000Z.json");
        FILE_NAMES.add("2021-12-08T06:31:12.000Z.json");
        FILE_NAMES.add("2021-12-08T15:31:12.000Z.json");
        FILE_NAMES.add("2021-12-09T06:31:15.000Z.json");
        FILE_NAMES.add("2021-12-09T15:31:27.000Z.json");
        FILE_NAMES.add("2021-12-10T06:31:24.000Z.json");
        FILE_NAMES.add("2021-12-10T15:31:07.000Z.json");
        FILE_NAMES.add("2021-12-11T06:31:07.000Z.json");
        FILE_NAMES.add("2021-12-11T15:31:18.000Z.json");
        FILE_NAMES.add("2021-12-12T06:31:06.000Z.json");
        FILE_NAMES.add("2021-12-12T15:31:18.000Z.json");
        FILE_NAMES.add("2021-12-13T06:31:10.000Z.json");
        FILE_NAMES.add("2021-12-13T15:31:15.000Z.json");
        FILE_NAMES.add("2021-12-14T06:31:15.000Z.json");
        FILE_NAMES.add("2021-12-14T15:31:15.000Z.json");
        FILE_NAMES.add("2021-12-15T06:31:07.000Z.json");
        FILE_NAMES.add("2021-12-15T15:31:26.000Z.json");
        FILE_NAMES.add("2021-12-16T06:31:24.000Z.json");
        FILE_NAMES.add("2021-12-16T15:31:14.000Z.json");
        FILE_NAMES.add("2021-12-17T06:31:07.000Z.json");
        FILE_NAMES.add("2021-12-17T15:31:17.000Z.json");
        FILE_NAMES.add("2021-12-18T06:31:15.000Z.json");
        FILE_NAMES.add("2021-12-18T15:31:08.000Z.json");
        FILE_NAMES.add("2021-12-19T06:31:23.000Z.json");
        FILE_NAMES.add("2021-12-19T15:31:00.000Z.json");
        FILE_NAMES.add("2021-12-20T06:31:04.000Z.json");
        FILE_NAMES.add("2021-12-20T15:31:22.000Z.json");
        FILE_NAMES.add("2021-12-21T06:31:28.000Z.json");
        FILE_NAMES.add("2021-12-21T15:31:11.000Z.json");
        FILE_NAMES.add("2021-12-22T06:31:23.000Z.json");
        FILE_NAMES.add("2021-12-22T15:31:19.000Z.json");
        FILE_NAMES.add("2021-12-23T06:31:40.000Z.json");
        FILE_NAMES.add("2021-12-23T15:31:42.000Z.json");
        FILE_NAMES.add("2021-12-24T06:31:16.000Z.json");
        FILE_NAMES.add("2021-12-24T15:31:07.000Z.json");
        FILE_NAMES.add("2021-12-25T06:31:29.000Z.json");
        FILE_NAMES.add("2021-12-25T15:31:34.000Z.json");
        FILE_NAMES.add("2021-12-26T06:31:03.000Z.json");
        FILE_NAMES.add("2021-12-26T15:31:14.000Z.json");
        FILE_NAMES.add("2021-12-27T06:33:03.000Z.json");
        FILE_NAMES.add("2021-12-27T15:31:22.000Z.json");
        FILE_NAMES.add("2021-12-28T06:31:18.000Z.json");
        FILE_NAMES.add("2021-12-28T15:31:35.000Z.json");
        FILE_NAMES.add("2021-12-29T06:31:29.000Z.json");
        FILE_NAMES.add("2021-12-29T15:31:39.000Z.json");
        FILE_NAMES.add("2021-12-30T06:31:34.000Z.json");
        FILE_NAMES.add("2021-12-30T15:31:19.000Z.json");
        FILE_NAMES.add("2021-12-31T06:31:03.000Z.json");
        FILE_NAMES.add("2021-12-31T15:31:15.000Z.json");
        FILE_NAMES.add("2022-01-01T06:31:11.000Z.json");
        FILE_NAMES.add("2022-01-01T15:31:17.000Z.json");
        FILE_NAMES.add("2022-01-02T06:31:30.000Z.json");
        FILE_NAMES.add("2022-01-02T15:31:14.000Z.json");
        FILE_NAMES.add("2022-01-03T15:31:37.000Z.json");
        FILE_NAMES.add("2022-01-04T15:31:24.000Z.json");
        FILE_NAMES.add("2022-01-05T15:31:35.000Z.json");
        FILE_NAMES.add("2022-01-06T15:31:18.000Z.json");
        FILE_NAMES.add("2022-01-07T15:31:26.000Z.json");
        FILE_NAMES.add("2022-01-08T15:31:20.000Z.json");
        FILE_NAMES.add("2022-01-09T15:31:11.000Z.json");
        FILE_NAMES.add("2022-01-10T15:31:28.000Z.json");
        FILE_NAMES.add("2022-01-11T15:31:22.000Z.json");
        FILE_NAMES.add("2022-01-12T15:31:45.000Z.json");
        FILE_NAMES.add("2022-01-13T15:31:32.000Z.json");
        FILE_NAMES.add("2022-01-14T15:31:27.000Z.json");
        FILE_NAMES.add("2022-01-15T15:31:25.000Z.json");
        FILE_NAMES.add("2022-01-16T15:31:07.000Z.json");
        FILE_NAMES.add("2022-01-17T15:31:37.000Z.json");
        FILE_NAMES.add("2022-01-18T15:31:18.000Z.json");
        FILE_NAMES.add("2022-01-19T15:31:37.000Z.json");
        FILE_NAMES.add("2022-01-20T15:31:32.000Z.json");
        FILE_NAMES.add("2022-01-21T15:31:44.000Z.json");
        FILE_NAMES.add("2022-01-22T15:31:24.000Z.json");
        FILE_NAMES.add("2022-01-23T15:31:51.000Z.json");
        FILE_NAMES.add("2022-01-24T15:31:27.000Z.json");
        FILE_NAMES.add("2022-01-25T15:31:16.000Z.json");
        FILE_NAMES.add("2022-01-26T15:31:36.000Z.json");
        FILE_NAMES.add("2022-01-27T15:31:23.000Z.json");
        FILE_NAMES.add("2022-01-28T15:31:25.000Z.json");
        FILE_NAMES.add("2022-01-29T15:31:19.000Z.json");
        FILE_NAMES.add("2022-01-30T15:31:24.000Z.json");
        FILE_NAMES.add("2022-01-31T15:31:53.000Z.json");
        FILE_NAMES.add("2022-02-01T15:31:26.000Z.json");
        FILE_NAMES.add("2022-02-02T15:31:38.000Z.json");
        FILE_NAMES.add("2022-02-03T15:31:36.000Z.json");
        FILE_NAMES.add("2022-02-04T15:31:42.000Z.json");
        FILE_NAMES.add("2022-02-05T15:31:34.000Z.json");
        FILE_NAMES.add("2022-02-06T15:31:15.000Z.json");
        FILE_NAMES.add("2022-02-07T15:31:41.000Z.json");
        FILE_NAMES.add("2022-02-08T15:31:32.000Z.json");
        FILE_NAMES.add("2022-02-09T15:31:22.000Z.json");
        FILE_NAMES.add("2022-02-10T15:31:35.000Z.json");
        FILE_NAMES.add("2022-02-11T15:31:31.000Z.json");
        FILE_NAMES.add("2022-02-12T15:31:24.000Z.json");
        FILE_NAMES.add("2022-02-13T15:31:23.000Z.json");
        FILE_NAMES.add("2022-02-14T15:31:23.000Z.json");
        FILE_NAMES.add("2022-02-15T15:31:41.000Z.json");
        FILE_NAMES.add("2022-02-16T15:31:33.000Z.json");
        FILE_NAMES.add("2022-02-17T15:31:44.000Z.json");
        FILE_NAMES.add("2022-02-18T15:31:27.000Z.json");
        FILE_NAMES.add("2022-02-19T15:31:29.000Z.json");
        FILE_NAMES.add("2022-02-20T15:31:27.000Z.json");
        FILE_NAMES.add("2022-02-21T15:31:25.000Z.json");
        FILE_NAMES.add("2022-02-22T15:31:19.000Z.json");
        FILE_NAMES.add("2022-02-23T15:31:29.000Z.json");
        FILE_NAMES.add("2022-02-24T15:31:16.000Z.json");

    }

    public static Map<String, String> MUNICIPALITY_OVERRIDE = new LinkedHashMap<>();
    static {
        MUNICIPALITY_OVERRIDE.put("Bruck an der Glocknerstraße", "Bruck an der Großglocknerstraße");
        MUNICIPALITY_OVERRIDE.put("Fusch an der Glocknerstraße", "Fusch an der Großglocknerstraße");
        MUNICIPALITY_OVERRIDE.put("Hollersbach", "Hollersbach im Pinzgau");
        MUNICIPALITY_OVERRIDE.put("Sankt Martin im Tennengebirge", "Sankt Martin am Tennengebirge");
        MUNICIPALITY_OVERRIDE.put("Rußbach am Pass Gschütt", "Rußbach am Paß Gschütt");
        MUNICIPALITY_OVERRIDE.put("Salzburg Stadt", "Salzburg");
    }

    public static void main(String[] args) throws Exception {

        C19Application.init("");
        C19Application.getInstance().addSubConfig(OutboundHttpConfig.proxy("127.0.0.1", 8888));
        C19Application.getInstance().addSubConfig(OutboundHttpConfig.noopSsl());

        JsonTypeImplHexmapDataRoot dataRoot = new JsonTypeImplHexmapDataRoot("temp.json");

        // build a map of GKZ and Gemeindename
        loadMunicipalityData();

        Map<String, Integer> populationByGkz = new HashMap<>();
        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(FOLDER_______BASE, FILE___POPULATION)), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> populationCsvDatasetFactory = new DataSetFactoryImplCsv();
            IDataSet<String, Long> populationCsvDataSet = populationCsvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> populationCsvRecords = populationCsvDataSet.getEntriesY();

            for (IDataEntry<String, Long> caseCsvRecord : populationCsvRecords) {

                String gkz = caseCsvRecord.optValue("gkz", FieldTypes.STRING).orElseThrow();
                int population = caseCsvRecord.optValue("pop", FieldTypes.DOUBLE).orElseThrow().intValue();

                populationByGkz.put(gkz, population);

            }

        }

        Map<String, Double> lastExposedByGkz = new HashMap<>();
        Map<String, Double> correctionByGkz = new HashMap<>();
//        for (Entry<String, String> keysetMunicipalityEntry : KEYSET_MUNICIPALITY.entrySet()) {
//            lastValuesByMunicipality.put(keysetMunicipalityEntry.getKey(), 0D);
//            corrValuesByMunicipality.put(keysetMunicipalityEntry.getKey(), 0D);
//        }

        for (String fileName : FILE_NAMES) {

            Date date = DATE_FORMAT___GITHUB.parse(fileName.substring(0, 10));
            dataRoot.clearData(date);

            System.out.println(date);
            String outputDate = DATE_FORMAT_____FILE.format(date);

            String formattedGithubUrl = String.format(URL_TEMPLATE, fileName);
            File formattedFile = new File(FOLDER_______BASE, String.format(FILE_TEMPLATE_JSON, outputDate));

            if (!formattedFile.exists()) {
                try {
                    loadMiscData(formattedGithubUrl, formattedFile);
                    System.out.println(formattedGithubUrl + " > " + formattedFile);
                } catch (Exception ex) {
                    System.err.println(ex);
                }

            }

            JsonTypeImplSalzburgRoot sbgRoot = new ObjectMapper().readValue(new FileInputStream(formattedFile), JsonTypeImplSalzburgRoot.class);

            for (Entry<String, Map<String, JsonTypeImplSalzburgData>> sbgDistrictEntry : sbgRoot.getData().entrySet()) {

                String districtName = sbgDistrictEntry.getKey();
                for (Entry<String, JsonTypeImplSalzburgData> sbgMunicipalityEntry : sbgDistrictEntry.getValue().entrySet()) {

                    String municipalityName = sbgMunicipalityEntry.getKey();
                    if (MUNICIPALITY_OVERRIDE.containsKey(municipalityName)) {
                        municipalityName = MUNICIPALITY_OVERRIDE.get(municipalityName);
                    }

                    if (municipalityName.equals(districtName) && municipalityName.endsWith("gau") || municipalityName.contains("Nicht zugeordnet")) {
//                        System.out.println("ignore: " + municipalityName);
                        continue;
                    }
//                    System.out.println();

                    String mnc = municipalityName;
                    Optional<Entry<String, String>> oMunicipalityEntry = KEYSET_MUNICIPIALITY.entrySet().stream().filter(m -> m.getValue().equalsIgnoreCase(mnc)).findFirst();
                    if (oMunicipalityEntry.isEmpty()) {

                        System.out.println("missing: " + municipalityName + " / " + districtName);

                    } else {

                        String gkz = oMunicipalityEntry.get().getKey();
                        String gesamt = sbgMunicipalityEntry.getValue().getGesamt();

                        if (!correctionByGkz.containsKey(gkz)) {
                            correctionByGkz.put(gkz, 0D);
                        }

                        double exposed = Integer.parseInt(gesamt);
                        double population = populationByGkz.get(gkz);

                        if (!lastExposedByGkz.containsKey(gkz)) {
                            lastExposedByGkz.put(gkz, exposed);
                        }

                        if (lastExposedByGkz.get(gkz) > exposed) {
                            correctionByGkz.put(gkz, correctionByGkz.get(gkz) + lastExposedByGkz.get(gkz) - exposed);
//                            System.out.println("correct @ " + municipalityName + " / " + correctionByGkz.get(gkz) + " / " + correctionByGkz.get(gkz) / populationByGkz.get(gkz));
                        }

                        dataRoot.addData(date, gkz, 0, exposed); //  + correctionByGkz.get(gkz)
                        dataRoot.addData(date, gkz, 1, population);

                        lastExposedByGkz.put(gkz, exposed);

                        for (Entry<String, String> keysetMunicipalityEntry : KEYSET_MUNICIPALITY.entrySet()) {
                            String prefixKey = keysetMunicipalityEntry.getKey().replaceAll("#", "");
                            if (gkz.startsWith(prefixKey)) {
                                KEYSET_MUNICIPIALITY.put(keysetMunicipalityEntry.getKey(), keysetMunicipalityEntry.getValue());
                                dataRoot.addData(date, keysetMunicipalityEntry.getKey(), 0, exposed); //  + correctionByGkz.get(gkz)
                                dataRoot.addData(date, keysetMunicipalityEntry.getKey(), 1, population);
                            }
                        }

                    }

                }

            }

        }

        for (Entry<String, Double> correctionByGkzEntry : correctionByGkz.entrySet()) {
            Optional<Entry<String, String>> oMunicipalityEntry = KEYSET_MUNICIPIALITY.entrySet().stream().filter(m -> m.getKey().equalsIgnoreCase(correctionByGkzEntry.getKey())).findFirst();
            if (oMunicipalityEntry.isPresent()) {
                System.out.println(String.format("%-35s; %5s; %6.0f; %6.4f", oMunicipalityEntry.get().getValue(), oMunicipalityEntry.get().getKey(), correctionByGkzEntry.getValue(),
                            correctionByGkzEntry.getValue() / lastExposedByGkz.get(correctionByGkzEntry.getKey())));

            }

        }

        List<String> keys1 = new ArrayList<>(KEYSET_MUNICIPIALITY.keySet());
        keys1.sort((a, b) -> {
            return a.compareTo(b);
        });
        Map<String, String> municipalityMapSorted = new LinkedHashMap<>();
        for (String key1 : keys1) {
            if (key1.startsWith("5")) {
                municipalityMapSorted.put(key1, KEYSET_MUNICIPIALITY.get(key1));
            }
        }

        JsonTypeImplHexmapDataRoot fileRoot = new JsonTypeImplHexmapDataRoot("hexmap-data-x4-incidence-salzburg.json");

        fileRoot.addKeyset("Gemeinde", municipalityMapSorted);
        fileRoot.addIdx("Fälle", 0, 10000, false);
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

        String hexmapFilePath = FOLDER_______BASE + "/hexmap-data-salzburg-gemeinde.json";
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(hexmapFilePath), fileRoot); //

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

    static void loadMunicipalityData() throws MalformedURLException {

        // read feature
        IHttpRequest.GET<JsonTypeImplQueryResultPoint> queryRequest = HttpRequest.GET.create("https://nbfleischer.int.vertigis.com/server/rest/services/pnt_full4/MapServer/0/query",
                    ResponseHandler.forJsonTyped(JsonTypeImplQueryResultPoint.class));
        queryRequest.setParameter("where", "1=1");
        queryRequest.setParameter("outFields", "GKZ, PG");
        queryRequest.setParameter("returnGeometry", "false");
        queryRequest.setParameter("orderByFields", "GKZ");
        queryRequest.setParameter("returnDistinctValues", "true");
        queryRequest.setParameter("f", "pjson");

        URL url = queryRequest.getUri().toURL();
        try (InputStream columnInput = url.openConnection().getInputStream()) {

            IDataSet<String, Long> datset = new DataSetFactoryImplFeature().createDataSet(columnInput, StandardCharsets.UTF_8);
            List<IDataEntry<String, Long>> dataEntries = datset.getEntriesY();

            for (IDataEntry<String, Long> dataEntry : dataEntries) {

                Optional<String> oGkn = dataEntry.optValue("PG", FieldTypes.STRING);
                if (oGkn.isPresent()) {

                    String gkzString = dataEntry.optValue("GKZ", FieldTypes.STRING).orElseGet(() -> "-1");
                    int gkzInteger = Integer.parseInt(gkzString);
                    KEYSET_MUNICIPIALITY.put(String.valueOf(gkzInteger), oGkn.get());

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
