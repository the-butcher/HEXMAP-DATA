package com.igorion.hexmap.mortality;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igorion.hexmap.mortality.impl.LoessWrapper;
import com.igorion.hexmap.mortality.impl.Mortality;
import com.igorion.hexmap.mortality.impl.MortalityImpl;
import com.igorion.hexmap.mortality.impl.NutsRegions;
import com.igorion.hexmap.mortality.impl.ValueDateMap;
import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IDataSet;
import com.igorion.report.dataset.IDataSetFactory;
import com.igorion.report.dataset.impl.DataSetFactoryImplCsv;
import com.igorion.report.value.FieldTypeImplDate;
import com.igorion.report.value.FieldTypes;
import com.igorion.type.json.impl.JsonTypeImplMortalityDataItem;
import com.igorion.type.json.impl.JsonTypeImplMortalityDataRoot;
import com.igorion.util.impl.DateUtil;
import com.igorion.util.impl.Statistics;

public class MortalityParserDistrict01 {

    static final Color COLOR_AVERAGE_MORTALITY = Color.GRAY;

    public static final File FOLDER_________BASE = new File("C:\\privat\\_projects_cov\\covid2019_hexmap_data\\mortality");
    public static final File FILE_GKZ_POPULATION = new File(FOLDER_________BASE, "population_district_2002_2021_00_n5_99.csv");
    public static final File FILE_NUTS1_AGE_WEEK = new File(FOLDER_________BASE, "OGD_gest_kalwo_alter_GEST_KALWOCHE_5J_100.csv");
    public static final File FILE_INCIDENCES_EMS = new File(FOLDER_________BASE, "incidences_ems_full.csv");

    static final Date POP_DATE_A = new Date(2020 - 1900, 0, 1);
    // static final Date POP_DATE_B = new Date(2022 - 1900, 0, 1);
//    static final Date POP_DATE_B = new Date(2022 - 1900, 2, 10);
    static final Date POP_DATE_B = toDate("KALW-202231");
    static final Date POP_DATE_C = new Date(2022 - 1900, 11, 1);

    static final Date POP_DATE_STATS_A = new Date(POP_DATE_A.getTime() - DateUtil.MILLISECONDS_PER__DAY * 7 * 12);
    static final Date POP_DATE_STATS_B = new Date(POP_DATE_C.getTime() + DateUtil.MILLISECONDS_PER__DAY * 7 * 12); // 52 * 2 + 12

    static final long MIN_IMAGE_INSTANT = POP_DATE_A.getTime() - DateUtil.MILLISECONDS_PER__DAY * 48;
    static final long MAX_IMAGE_INSTANT = POP_DATE_C.getTime() + DateUtil.MILLISECONDS_PER__DAY * 6;

    static final double MIN_IMAGE_Y = -7.5;
    static final double MAX_IMAGE_Y = 53.5;

    public static final SimpleDateFormat DATE_FORMAT_MMYY = new SimpleDateFormat("MM.yyyy");
    public static final SimpleDateFormat DATE_FORMAT_JS = new SimpleDateFormat("yyyy-MM-dd");

    static final int MIN_REF_YEAR = 2015;
    static final int MAX_REF_YEAR = 2019;

    public static void main(String[] args) throws Exception {

        System.out.println("POP_DATE_B: " + POP_DATE_B);
        System.out.println("POP_DATE_C: " + POP_DATE_C);
        System.out.println("POP_DATE_STATS_A: " + POP_DATE_STATS_A);
        System.out.println("POP_DATE_STATS_B: " + POP_DATE_STATS_B);

        Map<String, INutsRegion> nutsRegions = NutsRegions.getRegions(FILE_GKZ_POPULATION, EAgeGroup.values());

        Map<String, IMortality> nutsMortality = new LinkedHashMap<>();
        Map<String, LoessWrapper> nutsLoessWrappers = new LinkedHashMap<>();
        Map<String, ValueDateMap> incidences = new LinkedHashMap<>();

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_INCIDENCES_EMS), StandardCharsets.UTF_8))) {

            FieldTypeImplDate fieldTypeDate = new FieldTypeImplDate(DATE_FORMAT_JS, ".*?");

            IDataSetFactory<String, Long> csvDatasetFactory = new DataSetFactoryImplCsv(";");
            IDataSet<String, Long> csvDataSet = csvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> csvRecords = csvDataSet.getEntriesY();

            // date;gkn;gkz;pop;cases;incidence
            for (IDataEntry<String, Long> csvRecord : csvRecords) {

                Date date = csvRecord.optValue("date", fieldTypeDate).orElseThrow();
                String bkz = csvRecord.optValue("gkz", FieldTypes.STRING).orElseThrow();
                double incidence = csvRecord.optValue("incidence", FieldTypes.DOUBLE).orElseThrow();

                incidences.computeIfAbsent(bkz, n -> new ValueDateMap()).addValue(EAgeGroup.ETOTAL, date, incidence);

            }

        }

        /**
         * read mortality (reaching back in history to 2000)
         */
        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_NUTS1_AGE_WEEK), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> csvDatasetFactory = new DataSetFactoryImplCsv(";");
            IDataSet<String, Long> csvDataSet = csvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> csvRecords = csvDataSet.getEntriesY();

            // C-KALWOCHE-0;C-POLBEZKW-0;C-SEXWO-0;F-ANZ-1;F-RATE-1
            for (IDataEntry<String, Long> csvRecord : csvRecords) {

                String bkz = toSanitizedBkz(csvRecord.optValue("C-B00-0", FieldTypes.STRING).orElseThrow().substring("B00-".length()));
                Date date = toDate(csvRecord.optValue("C-KALWOCHE-0", FieldTypes.STRING).orElseThrow());

                String ageGroupRaw = csvRecord.optValue("C-ALTER5-0", FieldTypes.STRING).orElseThrow();
                EAgeGroup ageGroup = EAgeGroup.optAgeGroupByCAlter5(ageGroupRaw).orElseThrow();

                String deathsRaw = csvRecord.optValue("F-ANZ-1", FieldTypes.STRING).orElseThrow();
                double deaths = Double.parseDouble(deathsRaw.replace(",", "."));

                nutsMortality.computeIfAbsent(bkz, n -> new MortalityImpl()).addDeaths(ageGroup, date, deaths);
                nutsMortality.computeIfAbsent("###", n -> new MortalityImpl()).addDeaths(ageGroup, date, deaths);

                nutsMortality.computeIfAbsent(bkz, n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, date, deaths);
                nutsMortality.computeIfAbsent("###", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, date, deaths);

            }

        }

//        Date kw18Date = toDate("KALW-202218");
//        nutsMortality.computeIfAbsent("###", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw18Date, 1638);
//        nutsMortality.computeIfAbsent("1##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw18Date, 59);
//        nutsMortality.computeIfAbsent("2##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw18Date, 109);
//        nutsMortality.computeIfAbsent("3##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw18Date, 379);
//        nutsMortality.computeIfAbsent("4##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw18Date, 280);
//        nutsMortality.computeIfAbsent("5##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw18Date, 97);
//        nutsMortality.computeIfAbsent("6##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw18Date, 259);
//        nutsMortality.computeIfAbsent("7##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw18Date, 97);
//        nutsMortality.computeIfAbsent("8##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw18Date, 45);
//        nutsMortality.computeIfAbsent("9##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw18Date, 313);

//        Date kw19Date = toDate("KALW-202219");
//        nutsMortality.computeIfAbsent("###", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw19Date, 1546);
//        nutsMortality.computeIfAbsent("1##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw19Date, 51);
//        nutsMortality.computeIfAbsent("2##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw19Date, 108);
//        nutsMortality.computeIfAbsent("3##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw19Date, 306);
//        nutsMortality.computeIfAbsent("4##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw19Date, 257);
//        nutsMortality.computeIfAbsent("5##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw19Date, 97);
//        nutsMortality.computeIfAbsent("6##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw19Date, 232);
//        nutsMortality.computeIfAbsent("7##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw19Date, 125);
//        nutsMortality.computeIfAbsent("8##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw19Date, 66);
//        nutsMortality.computeIfAbsent("9##", n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, kw19Date, 304);

        /**
         * read mortality (2021 onwards)
         */
        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_NUTS1_AGE_WEEK), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> csvDatasetFactory = new DataSetFactoryImplCsv(";");
            IDataSet<String, Long> csvDataSet = csvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> csvRecords = csvDataSet.getEntriesY();

            /**
             *  read from interpolateable data and create loess curves from it (no normalization on these groups, since they act as base for normalization themselves)
             */
            for (IDataEntry<String, Long> csvRecord : csvRecords) {

                String bkz = toSanitizedBkz(csvRecord.optValue("C-B00-0", FieldTypes.STRING).orElseThrow().substring("B00-".length()));

                Date date = toDate(csvRecord.optValue("C-KALWOCHE-0", FieldTypes.STRING).orElseThrow());
                if (date.getTime() >= POP_DATE_STATS_A.getTime()) {

                    double fraction = toFraction(date);
                    double weeklyMortalityRate = nutsMortality.get(bkz).getNormalizedMortality(nutsRegions.get(bkz), date); // .getWeeklyMortality(nutsRegions.get(bkz), EAgeGroup.ETOTAL, date);
                    nutsLoessWrappers.computeIfAbsent(bkz, n -> new LoessWrapper(bkz, 0.06)).addValues(fraction, weeklyMortalityRate);

                    double weeklyMortalityRateX = nutsMortality.get("###").getNormalizedMortality(nutsRegions.get("###"), date); // .getWeeklyMortality(nutsRegions.get(bkz), EAgeGroup.ETOTAL, date);
                    nutsLoessWrappers.computeIfAbsent("###", n -> new LoessWrapper("###", 0.02)).addValues(fraction, weeklyMortalityRateX);

                }

            }

            Map<String, Statistics> statsReg = new HashMap<>(); // new Statistics();
            for (String displayNuts : nutsMortality.keySet()) {

                displayNuts = toSanitizedBkz(displayNuts);
//                if (!displayNuts.equals("5##")) {
//                    continue;
//                }

                IMortality refMortality = nutsMortality.get(displayNuts.substring(0, 1) + "##");
                INutsRegion refRegion = nutsRegions.get(displayNuts.substring(0, 1) + "##");

//                for (int year = 2018; year <= 2019; year++) {
//                    statsReg.computeIfAbsent(displayNuts, n -> new Statistics()).addValue(refMortality.getYearlyDeaths(EAgeGroup.ETOTAL, toDate("KALW-" + year + "01")));
//                }
//                double valComp = 0;
//                for (int year = 2020; year <= 2021; year++) {
//                    valComp += refMortality.getYearlyDeaths(EAgeGroup.ETOTAL, toDate("KALW-" + year + "01"));
//                }
//                valComp *= 0.5;

                // System.out.println(refRegion.getName() + " _ " + statsReg.get(displayNuts).getAverage() + " > " + valComp + " > " + valComp / statsReg.get(displayNuts).getAverage());

                JsonTypeImplMortalityDataRoot dataRoot = new JsonTypeImplMortalityDataRoot();
                dataRoot.setName(refRegion.getName());
                dataRoot.setMinYear(MIN_REF_YEAR);
                dataRoot.setMaxYear(MAX_REF_YEAR);

                BufferedImage bi = new BufferedImage(1200, 675, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = bi.createGraphics();
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
                g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                double[] imageCoordinate;
                // String displayNuts = "505";

                GeneralPath ciUpper95 = new GeneralPath();
                int countCiUpper95 = 0;

                GeneralPath ciLower95 = new GeneralPath();
                int countCiLower95 = 0;

                GeneralPath ciUpper68 = new GeneralPath();
                int countCiUpper68 = 0;

                GeneralPath ciLower68 = new GeneralPath();
                int countCiLower68 = 0;

                GeneralPath avgMort = new GeneralPath();
                int countAvgMort = 0;

                GeneralPath covMort = new GeneralPath();
                int countCovMort = 0;

                Map<Long, Statistics> statsRel = new HashMap<>(); // new Statistics();
                for (long instant = POP_DATE_STATS_A.getTime(); instant <= POP_DATE_STATS_B.getTime(); instant += DateUtil.MILLISECONDS_PER__DAY * 1) {

                    Date statsDate = new Date(instant);
                    for (int year = MIN_REF_YEAR; year <= MAX_REF_YEAR; year++) {

                        Date mappedDate = mapWeeklyDate(statsDate, year);

                        double mortalityVal0 = refMortality.getNormalizedMortality(refRegion, mappedDate);
                        statsRel.computeIfAbsent(statsDate.getTime(), t -> new Statistics()).addValue(mortalityVal0);

                    }

                }

                LoessWrapper avgInterpolator = new LoessWrapper("avg", 0.06);
                LoessWrapper stdInterpolator = new LoessWrapper("std", 0.03);
                for (long instant = POP_DATE_STATS_A.getTime(); instant <= POP_DATE_STATS_B.getTime(); instant += DateUtil.MILLISECONDS_PER__DAY * 1) {

                    Date tempDate = new Date(instant);
                    double fraction = toFraction(tempDate);

                    Statistics statistics = statsRel.get(tempDate.getTime());
                    avgInterpolator.addValues(fraction, statistics.getAverage());
                    stdInterpolator.addValues(fraction, statistics.getStandardDeviation());

                }

                for (long instant = POP_DATE_A.getTime(); instant <= POP_DATE_C.getTime(); instant += DateUtil.MILLISECONDS_PER__DAY * 1) {

                    Date tempDate = new Date(instant);
                    double fraction = toFraction(tempDate);

                    imageCoordinate = toImageCoordinate(bi, instant, avgInterpolator.getValue(fraction) + stdInterpolator.getValue(fraction) * 2);
                    if (countCiUpper95 == 0) {
                        ciUpper95.moveTo(imageCoordinate[0], bi.getHeight());
                    }
                    ciUpper95.lineTo(imageCoordinate[0], imageCoordinate[1]);
                    countCiUpper95++;

                    imageCoordinate = toImageCoordinate(bi, instant, avgInterpolator.getValue(fraction) - stdInterpolator.getValue(fraction) * 2);
                    if (countCiLower95 == 0) {
                        ciLower95.moveTo(imageCoordinate[0], bi.getHeight());
                    }
                    ciLower95.lineTo(imageCoordinate[0], imageCoordinate[1]);
                    countCiLower95++;

                    imageCoordinate = toImageCoordinate(bi, instant, avgInterpolator.getValue(fraction) + stdInterpolator.getValue(fraction));
                    if (countCiUpper68 == 0) {
                        ciUpper68.moveTo(imageCoordinate[0], bi.getHeight());
                    }
                    ciUpper68.lineTo(imageCoordinate[0], imageCoordinate[1]);
                    countCiUpper68++;

                    imageCoordinate = toImageCoordinate(bi, instant, avgInterpolator.getValue(fraction) - stdInterpolator.getValue(fraction));
                    if (countCiLower68 == 0) {
                        ciLower68.moveTo(imageCoordinate[0], bi.getHeight());
                    }
                    ciLower68.lineTo(imageCoordinate[0], imageCoordinate[1]);
                    countCiLower68++;

                    double[] imageCoordinateAvgMort = toImageCoordinate(bi, instant, avgInterpolator.getValue(fraction));
                    if (countAvgMort == 0) {
                        avgMort.moveTo(imageCoordinateAvgMort[0], imageCoordinateAvgMort[1]);
                    }
                    avgMort.lineTo(imageCoordinateAvgMort[0], imageCoordinateAvgMort[1]);
                    countAvgMort++;

                    JsonTypeImplMortalityDataItem dataItem = new JsonTypeImplMortalityDataItem();

                    if (instant <= POP_DATE_B.getTime()) {

                        double mortalityVal = nutsLoessWrappers.get(displayNuts).getValue(fraction); // = nutsLoessWrappers.get(bkz).getValue(fraction);

//                        System.out.println("mortVal: " + mortalityVal + ",");

                        // double mortalityVal = mortality.getWeeklyMortality(nutsRegion, EAgeGroup.ETOTAL, tempDate);
                        // System.out.println(String.format("tempDate: %s >> %6.4f >> %6.4f", tempDate, mortalityVal, statsAbs.getAverage()));

                        imageCoordinate = toImageCoordinate(bi, instant, mortalityVal);

                        if (countCovMort == 0) {
                            covMort.moveTo(imageCoordinate[0], imageCoordinate[1]);
                        }
                        covMort.lineTo(imageCoordinate[0], imageCoordinate[1]);
                        countCovMort++;

                        dataItem.setMortVal(mortalityVal);
                    }

                    dataItem.setMortAvg(avgInterpolator.getValue(fraction));
                    dataItem.setCi95Lower(avgInterpolator.getValue(fraction) - stdInterpolator.getValue(fraction) * 2);
                    dataItem.setCi95Upper(avgInterpolator.getValue(fraction) + stdInterpolator.getValue(fraction) * 2);
                    dataItem.setCi68Lower(avgInterpolator.getValue(fraction) - stdInterpolator.getValue(fraction) * 1);
                    dataItem.setCi68Upper(avgInterpolator.getValue(fraction) + stdInterpolator.getValue(fraction) * 1);

                    double incidence = incidences.get(displayNuts).getWeeklyValue(EAgeGroup.ETOTAL, tempDate);
                    dataItem.setIncidence(incidence);

                    dataRoot.addItem(tempDate, dataItem);

                }

                new ObjectMapper().writerWithDefaultPrettyPrinter()
                            .writeValue(new File("C:\\privat\\_projects_cov\\covid2019_model2\\COVID19-SEIR\\src\\main\\webapp\\data\\mortality-data-at-" + displayNuts.substring(0, 1) + ".json"), dataRoot); //

                imageCoordinate = toImageCoordinate(bi, POP_DATE_C.getTime(), 0);
                ciUpper95.lineTo(imageCoordinate[0], imageCoordinate[1]);

                imageCoordinate = toImageCoordinate(bi, POP_DATE_A.getTime(), 0);
                ciUpper95.lineTo(imageCoordinate[0], imageCoordinate[1]);

                imageCoordinate = toImageCoordinate(bi, POP_DATE_C.getTime(), 0);
                ciLower95.lineTo(imageCoordinate[0], imageCoordinate[1]);
                imageCoordinate = toImageCoordinate(bi, POP_DATE_A.getTime(), 0);
                ciLower95.lineTo(imageCoordinate[0], imageCoordinate[1]);

                ciLower95.lineTo(bi.getWidth(), bi.getHeight());
                ciLower95.lineTo(0, bi.getHeight());

                imageCoordinate = toImageCoordinate(bi, POP_DATE_C.getTime(), 0);
                ciUpper68.lineTo(imageCoordinate[0], imageCoordinate[1]);

                imageCoordinate = toImageCoordinate(bi, POP_DATE_A.getTime(), 0);
                ciUpper68.lineTo(imageCoordinate[0], imageCoordinate[1]);

                imageCoordinate = toImageCoordinate(bi, POP_DATE_C.getTime(), 0);
                ciLower68.lineTo(imageCoordinate[0], imageCoordinate[1]);
                imageCoordinate = toImageCoordinate(bi, POP_DATE_A.getTime(), 0);
                ciLower68.lineTo(imageCoordinate[0], imageCoordinate[1]);

                ciLower68.lineTo(bi.getWidth(), bi.getHeight());
                ciLower68.lineTo(0, bi.getHeight());

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setColor(new Color(0xdddddd));
                g2d.fill(ciUpper95);

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fill(ciUpper68);

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setColor(new Color(0xdddddd));
                g2d.fill(ciLower68);

                g2d.setColor(Color.WHITE);
                g2d.fill(ciLower95);

                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setColor(COLOR_AVERAGE_MORTALITY);
                g2d.draw(avgMort);

                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
                g2d.setColor(Color.BLACK);
                g2d.draw(covMort);

                double maxY = 50;
                for (double y = 0; y <= maxY; y += 5) {

                    GeneralPath chartLineY = new GeneralPath();
                    imageCoordinate = toImageCoordinate(bi, POP_DATE_C.getTime(), y);
                    chartLineY.moveTo(imageCoordinate[0], imageCoordinate[1]);
                    imageCoordinate = toImageCoordinate(bi, POP_DATE_A.getTime(), y);
                    chartLineY.lineTo(imageCoordinate[0], imageCoordinate[1]);

                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                    g2d.setColor(Color.BLACK);
                    g2d.draw(chartLineY);

                    // g2d.setFont(new Font("Consolas", 10, Font.PLAIN));
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    g2d.setColor(Color.BLACK);

                    drawRotate(g2d, 33, (int) imageCoordinate[1] + 3, 0, String.valueOf(y), 13);

                }

//                for (int year = 2016; year <= 2021; year++) {
//
//                    Date date = new Date(year - 1900, 1, 1);
//                    double yearlyMortality = refMortality.getYearlyMortality(refRegion, EAgeGroup.ETOTAL, date);
//                    System.out.println("yearly: " + date + " > " + yearlyMortality);
//
//                }

                for (int year = 2020; year <= 2022; year++) {

                    for (int month = 0; month < 12; month += 3) {

                        Date date = new Date(year - 1900, month, 1);
                        long instant = date.getTime();

                        GeneralPath chartLineX = new GeneralPath();
                        imageCoordinate = toImageCoordinate(bi, instant, maxY);
                        chartLineX.moveTo(imageCoordinate[0], imageCoordinate[1]);
                        imageCoordinate = toImageCoordinate(bi, instant, 0.0);
                        chartLineX.lineTo(imageCoordinate[0], imageCoordinate[1]);

                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                        g2d.setColor(Color.BLACK);
                        g2d.draw(chartLineX);

                        // g2d.setFont(new Font("Consolas", 10, Font.PLAIN));
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                        g2d.setColor(Color.BLACK);

                        drawRotate(g2d, imageCoordinate[0] + 4, imageCoordinate[1] + 53, -90, DATE_FORMAT_MMYY.format(date), 13);

                    }
                }

                int xPosPrimaryLabel = 450;

                drawRotate(g2d, xPosPrimaryLabel, 665, 0, "Datum", 17);
                drawRotate(g2d, 23, 480, -90, "Sterblichkeit / Woche / 100.000", 17);

                drawRotate(g2d, 890, 23, 0, "@FleischerHannes https://www.statistik.at", 13); // https://ec.europa.eu/eurostat/ // https://www.statistik.at
                // drawRotate(g2d, xPosPrimaryLabel, 23, 0, "Österreich / " + refRegion.getName() + " / " + nutsRegions.get(displayNuts).getName(), 17); //  (2010 - 2019)
                drawRotate(g2d, xPosPrimaryLabel, 23, 0, "Österreich / " + refRegion.getName(), 17); //  (2010 - 2019)
                // drawRotate(g2d, xPosPrimaryLabel, 23, 0, "Österreich", 17); //  (2010 - 2019)

                drawLegend(g2d, 740, 665, Color.BLACK, "Sterblichkeit");
                drawLegend(g2d, 890, 665, COLOR_AVERAGE_MORTALITY, "Sterblichkeit " + MIN_REF_YEAR + "-" + MAX_REF_YEAR + " / CI68 / CI95");

                ImageIO.write(bi, "png", new File("mortality_" + displayNuts + ".png"));
//                System.out.println(kw12Date);

            }

        }

    }

    public static double toFraction(Date date) {
        return (date.getTime() - POP_DATE_STATS_A.getTime()) * 1.0 / (POP_DATE_STATS_B.getTime() - POP_DATE_A.getTime());
    }

    @SuppressWarnings("deprecation")
    protected static Date mapWeeklyDate(Date date, int year) {
        return new Date(year - 1900, date.getMonth(), date.getDate());
    }

    protected static double[] toImageCoordinate(BufferedImage image, double instant, double y) {

        double xFrac = (instant - MIN_IMAGE_INSTANT) * 1.0 / (MAX_IMAGE_INSTANT - MIN_IMAGE_INSTANT);
        double yFrac = (y - MIN_IMAGE_Y) / (MAX_IMAGE_Y - MIN_IMAGE_Y);

        return new double[] {
                    image.getWidth() * xFrac,
                    image.getHeight() - image.getHeight() * yFrac
        };

    }

    public static void drawLegend(Graphics2D g2d, int x, int y, Color color, String text) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(x, y - 5, x + 30, y - 5);
        drawRotate(g2d, x + 40, y, 0, text, 13); // https://ec.europa.eu/eurostat/
    }

    public static void drawRotate(Graphics2D g2d, double x, double y, int angle, String text, int fontSize) {

        Font font = new Font("Consolas", Font.BOLD, fontSize);
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);

        g2d.translate((float) x, (float) y);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawString(text, 0, 0);
        g2d.rotate(-Math.toRadians(angle));
        g2d.translate(-(float) x, -(float) y);

    }

    public static Date toDate(String yearAndDate1) {

        String yearAndDate = yearAndDate1.substring("KALW-".length());

        int year = Integer.parseInt(yearAndDate.substring(0, 4));
        int week = Integer.parseInt(yearAndDate.substring(4, 6));

        return Mortality.toThursdayInWeek(year, week);

    }

    public static String toSanitizedBkz(String bkz1) {

        String bkz = bkz1;
        if (bkz.equals("0")) {
            bkz = "###";
        } else if (bkz.length() == 1) {
            bkz = bkz + "##";
        } else if (bkz.equals("900")) {
            bkz = "9##";
        }
        return bkz;

    }

}
