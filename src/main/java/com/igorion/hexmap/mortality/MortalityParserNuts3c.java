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

import com.igorion.hexmap.mortality.impl.LoessWrapper;
import com.igorion.hexmap.mortality.impl.Mortality;
import com.igorion.hexmap.mortality.impl.MortalityImpl;
import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IDataSet;
import com.igorion.report.dataset.IDataSetFactory;
import com.igorion.report.dataset.impl.DataSetFactoryImplCsv;
import com.igorion.report.value.FieldTypes;
import com.igorion.util.impl.DateUtil;
import com.igorion.util.impl.Statistics;

public class MortalityParserNuts3c {

    static final Color COLOR_AVERAGE_MORTALITY = Color.GRAY;

    public static final File FOLDER_________BASE = new File("C:\\privat\\_projects_cov\\covid2019_hexmap_data\\mortality");
//    public static final File FILE_GKZ_POPULATION = new File(FOLDER_________BASE, "hexcube-base-population_00_99.csv");
    public static final File FILE_NUTS1_AGE_WEEK = new File(FOLDER_________BASE, "OGD_rate_kalwo_GEST_KALWOCHE_STR_100.csv");
    public static final File FILE_NUTS3_AGE_WEEK = new File(FOLDER_________BASE, "OGD_rate_kalwobez_GEST_KALWOCHE_STR_BZ_100.csv");

    static final Date POP_DATE_A = new Date(2020 - 1900, 0, 1);
    // static final Date POP_DATE_B = new Date(2022 - 1900, 0, 1);
    static final Date POP_DATE_B = new Date(2022 - 1900, 1, 3);
    static final Date POP_DATE_C = new Date(2022 - 1900, 3, 1);

    static final Date POP_DATE_STATS_A = new Date(POP_DATE_A.getTime() - DateUtil.MILLISECONDS_PER__DAY * 7 * 12);
    static final Date POP_DATE_STATS_B = new Date(POP_DATE_A.getTime() + DateUtil.MILLISECONDS_PER__DAY * 7 * 120); // 52 * 2 + 12

    static final long MIN_IMAGE_INSTANT = POP_DATE_A.getTime() - DateUtil.MILLISECONDS_PER__DAY * 48;
    static final long MAX_IMAGE_INSTANT = POP_DATE_C.getTime() + DateUtil.MILLISECONDS_PER__DAY * 6;

    static final double MIN_IMAGE_Y = -7.5;
    static final double MAX_IMAGE_Y = 53.5;

    public static final SimpleDateFormat DATE_FORMAT_MMYY = new SimpleDateFormat("MM.yyyy");

    public static void main(String[] args) throws Exception {

        // Map<String, INutsRegion> nutsRegions = new LinkedHashMap<>(); // NutsRegions.getRegions(FILE_GKZ_POPULATION, EAgeGroup.ETOTAL);

        // read file with standardized rate and make some experiments with the LoessInterpolator class
        Map<String, IMortality> nutsMortality = new LinkedHashMap<>();
        Map<String, LoessWrapper> nutsLoessWrappers = new LinkedHashMap<>();
        Map<String, GeneralPath> nutsPathsMort = new LinkedHashMap<>();

        // read mortality for country and province (reaching back in history)
        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_NUTS1_AGE_WEEK), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> csvDatasetFactory = new DataSetFactoryImplCsv(";");
            IDataSet<String, Long> csvDataSet = csvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> csvRecords = csvDataSet.getEntriesY();

            // C-KALWOCHE-0;C-POLBEZKW-0;C-SEXWO-0;F-ANZ-1;F-RATE-1
            for (IDataEntry<String, Long> csvRecord : csvRecords) {

                String bkz = toSanitizedBkz(csvRecord.optValue("C-BLWO-0", FieldTypes.STRING).orElseThrow().substring("BLWO-".length()));
                Date date = toDate(csvRecord.optValue("C-KALWOCHE-0", FieldTypes.STRING).orElseThrow());
                String sex = csvRecord.optValue("C-SEXWO-0", FieldTypes.STRING).orElseThrow();
                if (sex.equals("SEXWO-0")) {

                    String deathsRaw = csvRecord.optValue("F-RATE-1", FieldTypes.STRING).orElseThrow();
                    double deaths = Double.parseDouble(deathsRaw.replace(",", ".")) * 100;

                    // nutsRegions.computeIfAbsent(bkz, n -> new NutsRegionImpl(n, n)).addDeaths(EAgeGroup.ETOTAL, date, deaths);
                    nutsMortality.computeIfAbsent(bkz, n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, date, deaths);

                }

            }

        }

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_NUTS3_AGE_WEEK), StandardCharsets.UTF_8))) {

            IDataSetFactory<String, Long> csvDatasetFactory = new DataSetFactoryImplCsv(";");
            IDataSet<String, Long> csvDataSet = csvDatasetFactory.createDataSet(csvReader);
            List<IDataEntry<String, Long>> csvRecords = csvDataSet.getEntriesY();

            // load a full set of data
            for (IDataEntry<String, Long> csvRecord : csvRecords) {

                String bkz = toSanitizedBkz(csvRecord.optValue("C-POLBEZKW-0", FieldTypes.STRING).orElseThrow().substring("POLBEZKW-".length()));
                if (bkz.indexOf("#") >= 0) {
                    continue; // skip country and province
                }

                String sex = csvRecord.optValue("C-SEXWO-0", FieldTypes.STRING).orElseThrow();
                if (sex.equals("SEXWO-0")) {

                    Date date = toDate(csvRecord.optValue("C-KALWOCHE-0", FieldTypes.STRING).orElseThrow());

                    String deathsRaw = csvRecord.optValue("F-RATE-1", FieldTypes.STRING).orElseThrow();
                    double deaths = Double.parseDouble(deathsRaw.replace(",", ".")) * 100;

                    nutsMortality.computeIfAbsent(bkz, n -> new MortalityImpl()).addDeaths(EAgeGroup.ETOTAL, date, deaths);

                }

            }

            // parse data
            for (IDataEntry<String, Long> csvRecord : csvRecords) {

                String bkz = toSanitizedBkz(csvRecord.optValue("C-POLBEZKW-0", FieldTypes.STRING).orElseThrow().substring("POLBEZKW-".length()));

                String sex = csvRecord.optValue("C-SEXWO-0", FieldTypes.STRING).orElseThrow();
                if (sex.equals("SEXWO-0")) {

                    Date date = toDate(csvRecord.optValue("C-KALWOCHE-0", FieldTypes.STRING).orElseThrow());
                    double fraction = toFraction(date);

                    double weeklyMortalityRate = nutsMortality.get(bkz).getWeeklyDeaths(EAgeGroup.ETOTAL, date); // .getWeeklyMortality(nutsRegions.get(bkz), EAgeGroup.ETOTAL, date);
                    nutsLoessWrappers.computeIfAbsent(bkz, n -> new LoessWrapper(bkz, 0.0315)).addValues(fraction, weeklyMortalityRate);

                }

            }

            for (String displayNuts : nutsMortality.keySet()) {

                displayNuts = toSanitizedBkz(displayNuts);

                if (displayNuts.indexOf("#") == -1) {
                    continue;
                }

                BufferedImage bi = new BufferedImage(1200, 675, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = bi.createGraphics();
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
                g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                double[] imageCoordinate;
                IMortality refMortality = nutsMortality.get(displayNuts.substring(0, 1) + "##");
                // INutsRegion refRegion = nutsRegions.get(displayNuts.substring(0, 1) + "##");
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
                for (long instant = POP_DATE_STATS_A.getTime(); instant <= POP_DATE_STATS_B.getTime(); instant += DateUtil.MILLISECONDS_PER__DAY * 7) {
                    Date tempDate = new Date(instant);
                    for (int year = 2010; year <= 2019; year++) {

                        Date date0 = mapWeeklyDate(tempDate, year);
                        double mortalityVal0 = refMortality.getWeeklyDeaths(EAgeGroup.ETOTAL, date0); // .getWeeklyMortality(refRegion, EAgeGroup.ETOTAL, date0);
                        statsRel.computeIfAbsent(tempDate.getTime(), t -> new Statistics()).addValue(mortalityVal0);

                    }
                }

                LoessWrapper avgInterpolator = new LoessWrapper("avg", 0.0315);
                LoessWrapper stdInterpolator = new LoessWrapper("std", 0.0315);
                for (long instant = POP_DATE_STATS_A.getTime(); instant <= POP_DATE_STATS_B.getTime(); instant += DateUtil.MILLISECONDS_PER__DAY * 7) {

                    Date tempDate = new Date(instant);
                    double fraction = toFraction(tempDate);

                    Statistics statistics = statsRel.get(tempDate.getTime());
                    avgInterpolator.addValues(fraction, statistics.getAverage());
                    stdInterpolator.addValues(fraction, statistics.getStandardDeviation());

                }

                for (long instant = POP_DATE_A.getTime(); instant <= POP_DATE_C.getTime(); instant += DateUtil.MILLISECONDS_PER__DAY) {

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

                    if (instant <= POP_DATE_B.getTime()) {

                        double mortalityVal = nutsLoessWrappers.get(displayNuts).getValue(fraction); // = nutsLoessWrappers.get(bkz).getValue(fraction);

                        // double mortalityVal = mortality.getWeeklyMortality(nutsRegion, EAgeGroup.ETOTAL, tempDate);
                        // System.out.println(String.format("tempDate: %s >> %6.4f >> %6.4f", tempDate, mortalityVal, statsAbs.getAverage()));

                        imageCoordinate = toImageCoordinate(bi, instant, mortalityVal);

                        if (countCovMort == 0) {
                            covMort.moveTo(imageCoordinate[0], imageCoordinate[1]);
                        }
                        covMort.lineTo(imageCoordinate[0], imageCoordinate[1]);
                        countCovMort++;

                    }

                }

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
                drawRotate(g2d, xPosPrimaryLabel, 23, 0, "Österreich / " + displayNuts, 17); //  (2010 - 2019)

                drawLegend(g2d, 740, 665, Color.BLACK, "Sterblichkeit");
                drawLegend(g2d, 890, 665, COLOR_AVERAGE_MORTALITY, "Sterblichkeit 2000-2019 / CI68 / CI95");

                ImageIO.write(bi, "png", new File("mortality_3c_" + displayNuts + ".png"));

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
