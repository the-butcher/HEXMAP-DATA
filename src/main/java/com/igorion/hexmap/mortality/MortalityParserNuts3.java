package com.igorion.hexmap.mortality;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import com.igorion.hexmap.mortality.impl.Mortality;
import com.igorion.hexmap.mortality.impl.Nuts3Regions;
import com.igorion.util.impl.DateUtil;
import com.igorion.util.impl.Statistics;

public class MortalityParserNuts3 {

    public static final File FOLDER_________BASE = new File("C:\\privat\\_projects_cov\\covid2019_hexmap_data\\mortality");
    public static final File FILE_NUTS3______GKZ = new File(FOLDER_________BASE, "nuts3_gkz.csv");
    public static final File FILE_NUTS3_AGE_WEEK = new File(FOLDER_________BASE, "nuts3_age_week.csv");
    public static final File FILE_GKZ_POPULATION = new File(FOLDER_________BASE, "hexcube-base-population_00n90.csv");

    public static void main(String[] args) throws Exception {

        List<INuts3Region> nuts3Regions = Nuts3Regions.getRegions();
        INuts3Region nuts3Region = nuts3Regions.get(0);
//        EAgeGroup ageGroup = EAgeGroup.E70_74;

        Date popDateA = new Date(2020 - 1900, 0, 1);
        Date popDateB = new Date(2022 - 1900, 0, 1);

        BufferedImage bi = new BufferedImage(1200, 675, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        g2d.setStroke(new BasicStroke(1f));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        IMortality mortality = Mortality.optMortalityByNuts3(nuts3Region.getNuts3()).orElseThrow();

//        for (EAgeGroup ageGroup2 : EAgeGroup.values()) {
//
//            Statistics yearlyStats = new Statistics();
//            for (int year = 2010; year < 2019; year++) {
//
//                Date yearlyDate = new Date(year - 1900, 0, 1);
////                System.out.println("yearlyDate: " + yearlyDate);
//
////                double mortalityVal = mortality.getYearlyDeaths(ageGroup2, yearlyDate);
//                double mortalityVal = mortality.getYearlyMortality(nuts3Region, ageGroup2, yearlyDate);
//                // double population = nuts3Region.getPopulation(ageGroup, mappedDate);
//                yearlyStats.addValue(mortalityVal);
//
//            }
//
//            double valuePer100 = yearlyStats.getAverage() / 1000;
//
//            System.out.println(String.format("%s; %10.4f; %10.4f;", ageGroup2.getName(), valuePer100, 0.1 / valuePer100));
//        }

        long minInstantA = popDateA.getTime() - DateUtil.MILLISECONDS_PER__DAY * 35;
        long minInstantB = popDateA.getTime();
        long maxInstant = popDateB.getTime();
        double yMax = 700;

        for (int i = EAgeGroup.values().length - 1; i >= 0; i--) {

            EAgeGroup ageGroup = EAgeGroup.values()[i];

            GeneralPath ciUpper = new GeneralPath();
            int countCiUpper = 0;
            double[] imageCoordinateCiUpper;

            GeneralPath ciLower = new GeneralPath();
            int countCiLower = 0;
            double[] imageCoordinateCiLower;

            GeneralPath avgMort = new GeneralPath();
            int countAvgMort = 0;

            GeneralPath covMort = new GeneralPath();
            int countCovMort = 0;

            for (long instant = minInstantB; instant <= maxInstant; instant += DateUtil.MILLISECONDS_PER__DAY) {

                Date tempDate = new Date(instant);

                Statistics stats = new Statistics();
                for (int year = 2010; year < 2019; year++) {

                    Date weeklyDate = mapWeeklyDate(tempDate, year);

                    double mortalityVal = mortality.getWeeklyMortality(nuts3Region, ageGroup, weeklyDate);
                    // double population = nuts3Region.getPopulation(ageGroup, mappedDate);
                    stats.addValue(mortalityVal);

                }

//            System.out.print(String.format("%6.4f;%6.4f;%6.4f;", stats.getAverage(), stats.getAverage() + stats.getStandardDeviation(), stats.getAverage() - stats.getStandardDeviation()));

                imageCoordinateCiUpper = toImageCoordinate(bi, (instant - minInstantA) * 1.0 / (maxInstant - minInstantA), (stats.getAverage() + stats.getStandardDeviation()) / yMax);
                if (countCiUpper == 0) {
                    ciUpper.moveTo(imageCoordinateCiUpper[0], bi.getHeight());
                }
                ciUpper.lineTo(imageCoordinateCiUpper[0], imageCoordinateCiUpper[1]);
                countCiUpper++;

                imageCoordinateCiLower = toImageCoordinate(bi, (instant - minInstantA) * 1.0 / (maxInstant - minInstantA), (stats.getAverage() - stats.getStandardDeviation()) / yMax);
                if (countCiLower == 0) {
                    ciLower.moveTo(imageCoordinateCiLower[0], bi.getHeight());
                }
                ciLower.lineTo(imageCoordinateCiLower[0], imageCoordinateCiLower[1]);
                countCiLower++;

                double[] imageCoordinateAvgMort = toImageCoordinate(bi, (instant - minInstantA) * 1.0 / (maxInstant - minInstantA), stats.getAverage() / yMax);
                if (countAvgMort == 0) {
                    avgMort.moveTo(0, imageCoordinateAvgMort[1]);
                    avgMort.lineTo(imageCoordinateAvgMort[0], imageCoordinateAvgMort[1]);
//                    g2d.setFont(new Font("Consolas", 8, Font.PLAIN));
                    g2d.setColor(Color.BLACK);
                    if (i > 12) {
                        g2d.drawString(ageGroup.getName(), 0, (int) imageCoordinateAvgMort[1] - 3);
                    }
                }
                avgMort.lineTo(imageCoordinateAvgMort[0], imageCoordinateAvgMort[1]);
                countAvgMort++;

                // Date mappedDate = mapWeeklyDate(tempDate, tempDate.getYear() + 1900);
                double mortalityVal = mortality.getWeeklyMortality(nuts3Region, ageGroup, tempDate);
                double[] imageCoordinateCovMort = toImageCoordinate(bi, (instant - minInstantA) * 1.0 / (maxInstant - minInstantA), mortalityVal / yMax);
                if (countCovMort == 0) {
                    covMort.moveTo(0, imageCoordinateCovMort[1]);
                    covMort.lineTo(imageCoordinateCovMort[0], imageCoordinateCovMort[1]);
                }
                covMort.lineTo(imageCoordinateCovMort[0], imageCoordinateCovMort[1]);
                countCovMort++;

//                for (int year = 2020; year < 2022; year++) {
//
//                    Date mappedDate = mapWeeklyDate(tempDate, year);
//                    double mortalityVal = mortality.getWeeklyMortality(nuts3Region, ageGroup, mappedDate);
//
////                System.out.print(String.format("%6.4f;", mortalityVal));
//
//                }
//            System.out.println("");

            }

            imageCoordinateCiUpper = toImageCoordinate(bi, (minInstantB - minInstantA) * 1.0 / (maxInstant - minInstantA), 0);
            ciUpper.lineTo(bi.getWidth(), bi.getHeight());
            ciUpper.lineTo(imageCoordinateCiUpper[0], imageCoordinateCiUpper[1]);

            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fill(ciUpper);

            imageCoordinateCiLower = toImageCoordinate(bi, (minInstantB - minInstantA) * 1.0 / (maxInstant - minInstantA), 0);
            ciLower.lineTo(bi.getWidth(), bi.getHeight());
            ciLower.lineTo(imageCoordinateCiLower[0], bi.getHeight());

            ciLower.lineTo(bi.getWidth(), bi.getHeight());
            ciLower.lineTo(0, bi.getHeight());

            g2d.setColor(Color.WHITE);
            g2d.fill(ciLower);

            g2d.setColor(Color.GRAY);
            g2d.draw(avgMort);

            g2d.setColor(Color.RED);
            g2d.draw(covMort);

        }

        ImageIO.write(bi, "png", new File("mortality.png"));

    }

    protected static double[] toImageCoordinate(BufferedImage image, double xFrac, double yFrac) {

        return new double[] {
                    image.getWidth() * xFrac,
                    image.getHeight() - image.getHeight() * yFrac
        };

    }

    @SuppressWarnings("deprecation")
    protected static Date mapWeeklyDate(Date date, int year) {
        return new Date(year - 1900, date.getMonth(), date.getDate());
    }

    protected static Date mapYearlyDate(Date date, int year) {
        return new Date(year - 1900, 0, 1);
    }

}
