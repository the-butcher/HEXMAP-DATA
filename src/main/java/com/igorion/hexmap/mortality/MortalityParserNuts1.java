package com.igorion.hexmap.mortality;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import com.igorion.hexmap.mortality.impl.Mortality;
import com.igorion.hexmap.mortality.impl.NutsRegions;
import com.igorion.util.impl.DateUtil;
import com.igorion.util.impl.Statistics;

public class MortalityParserNuts1 {

    static final Date POP_DATE_A = new Date(2020 - 1900, 0, 1);
    static final Date POP_DATE_B = new Date(2022 - 1900, 0, 13);

    static final long MIN_IMAGE_INSTANT = POP_DATE_A.getTime() - DateUtil.MILLISECONDS_PER__DAY * 45;
    static final long MAX_IMAGE_INSTANT = POP_DATE_B.getTime() + DateUtil.MILLISECONDS_PER__DAY * 6;

    static final double MIN_IMAGE_Y = -5.5;
    static final double MAX_IMAGE_Y = 37.5;

    public static final SimpleDateFormat DATE_FORMAT_MMYY = new SimpleDateFormat("MM.yyyy");

    public static final File FOLDER_________BASE = new File("C:\\privat\\_projects_cov\\covid2019_hexmap_data\\mortality");
//    public static final File FILE_NUTS1______GKZ = new File(FOLDER_________BASE, "nuts1_gkz.csv");
    public static final File FILE_NUTS1_AGE_WEEK = new File(FOLDER_________BASE, "nuts1_age_week.csv");
    public static final File FILE_GKZ_POPULATION = new File(FOLDER_________BASE, "population_nuts1_00n90.csv");

    public static void main(String[] args) throws Exception {

        List<INutsRegion> nutsRegions = NutsRegions.getRegions(FILE_GKZ_POPULATION);
        INutsRegion nutsRegion = nutsRegions.stream().filter(r -> r.getNuts().equals("AT")).findFirst().orElseThrow();
        System.out.println(nutsRegion.getNuts() + " / " + nutsRegion.getPopulation(EAgeGroup.ETOTAL, POP_DATE_B));

        double population = nutsRegion.getPopulation(EAgeGroup.ETOTAL, POP_DATE_B);
        double weeklyDeaths = 0;
        double extraMortality = weeklyDeaths * 100000 / population;
        System.out.println("extraMortality: " + extraMortality);

        BufferedImage bi = new BufferedImage(1200, 675, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        g2d.setStroke(new BasicStroke(1f));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        IMortality mortality = Mortality.optMortalityByNuts3(nutsRegion.getNuts(), FILE_NUTS1_AGE_WEEK).orElseThrow();

        for (int i = 0 /* EAgeGroup.values().length - 1 */; i >= 0; i--) {

            EAgeGroup ageGroup = EAgeGroup.values()[i];
            double[] imageCoordinate;

            GeneralPath ciUpper = new GeneralPath();
            int countCiUpper = 0;

            GeneralPath ciLower = new GeneralPath();
            int countCiLower = 0;

            GeneralPath avgMort = new GeneralPath();
            int countAvgMort = 0;

            GeneralPath covMort = new GeneralPath();
            int countCovMort = 0;

//            GeneralPath covPlus = new GeneralPath();
//            int countCovPlus = 0;

            for (long instant = POP_DATE_A.getTime(); instant <= POP_DATE_B.getTime(); instant += DateUtil.MILLISECONDS_PER__DAY) {

                Date tempDate = new Date(instant);

                Statistics statsRel = new Statistics();
                Statistics statsAbs = new Statistics();
                for (int year = 2010; year <= 2019; year++) {

                    Date weeklyDate = mapWeeklyDate(tempDate, year);

                    double valRel = mortality.getWeeklyMortality(nutsRegion, ageGroup, weeklyDate);
                    double valAbs = mortality.getWeeklyDeaths(ageGroup, weeklyDate);

                    // double population = nuts3Region.getPopulation(ageGroup, mappedDate);
                    statsRel.addValue(valRel);
                    statsAbs.addValue(valAbs);

                }

                imageCoordinate = toImageCoordinate(bi, instant, statsRel.getAverage() + statsRel.getStandardDeviation() + extraMortality);
                if (countCiUpper == 0) {
                    ciUpper.moveTo(imageCoordinate[0], bi.getHeight());
                }
                ciUpper.lineTo(imageCoordinate[0], imageCoordinate[1]);
                countCiUpper++;

                imageCoordinate = toImageCoordinate(bi, instant, statsRel.getAverage() - statsRel.getStandardDeviation() + extraMortality);
                if (countCiLower == 0) {
                    ciLower.moveTo(imageCoordinate[0], bi.getHeight());
                }
                ciLower.lineTo(imageCoordinate[0], imageCoordinate[1]);
                countCiLower++;

                double[] imageCoordinateAvgMort = toImageCoordinate(bi, instant, statsRel.getAverage() + extraMortality);
                if (countAvgMort == 0) {
                    avgMort.moveTo(imageCoordinateAvgMort[0], imageCoordinateAvgMort[1]);
                }
                avgMort.lineTo(imageCoordinateAvgMort[0], imageCoordinateAvgMort[1]);
                countAvgMort++;

                double mortalityVal = mortality.getWeeklyMortality(nutsRegion, ageGroup, tempDate);

                if (mortalityVal > 0) {

                    // System.out.println(String.format("tempDate: %s >> %6.4f >> %6.4f", tempDate, mortalityVal, statsAbs.getAverage()));
                    imageCoordinate = toImageCoordinate(bi, instant, mortalityVal);

                    if (countCovMort == 0) {
                        covMort.moveTo(imageCoordinate[0], imageCoordinate[1]);
                    }
                    covMort.lineTo(imageCoordinate[0], imageCoordinate[1]);
                    countCovMort++;

                }

            }

            imageCoordinate = toImageCoordinate(bi, POP_DATE_B.getTime(), 0);
            ciUpper.lineTo(imageCoordinate[0], imageCoordinate[1]);

            imageCoordinate = toImageCoordinate(bi, POP_DATE_A.getTime(), 0);
            ciUpper.lineTo(imageCoordinate[0], imageCoordinate[1]);

            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fill(ciUpper);

            imageCoordinate = toImageCoordinate(bi, POP_DATE_B.getTime(), 0);
            ciLower.lineTo(imageCoordinate[0], imageCoordinate[1]);
            imageCoordinate = toImageCoordinate(bi, POP_DATE_A.getTime(), 0);
            ciLower.lineTo(imageCoordinate[0], imageCoordinate[1]);

            ciLower.lineTo(bi.getWidth(), bi.getHeight());
            ciLower.lineTo(0, bi.getHeight());

            g2d.setColor(Color.WHITE);
            g2d.fill(ciLower);

            for (double y = 0; y <= 35; y += 5) {

                GeneralPath chartLineY = new GeneralPath();
                imageCoordinate = toImageCoordinate(bi, POP_DATE_B.getTime(), y);
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
                    imageCoordinate = toImageCoordinate(bi, instant, 35);
                    chartLineX.moveTo(imageCoordinate[0], imageCoordinate[1]);
                    imageCoordinate = toImageCoordinate(bi, instant, 0.0);
                    chartLineX.lineTo(imageCoordinate[0], imageCoordinate[1]);

                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                    g2d.setColor(Color.BLACK);
                    g2d.draw(chartLineX);

                    // g2d.setFont(new Font("Consolas", 10, Font.PLAIN));
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    g2d.setColor(Color.BLACK);

                    drawRotate(g2d, imageCoordinate[0] + 5, imageCoordinate[1] + 53, -90, DATE_FORMAT_MMYY.format(date), 13);

                }
            }

            drawRotate(g2d, 550, 665, 0, "Datum", 17);
            drawRotate(g2d, 23, 480, -90, "Sterblichkeit / Woche / 100.000", 17);
            drawRotate(g2d, 550, 23, 0, "Österreich (2010 - 2019)", 17);

            drawRotate(g2d, 950, 665, 0, "https://ec.europa.eu/eurostat/", 13);

            g2d.setColor(Color.GRAY);
            g2d.draw(avgMort);

            g2d.setStroke(new BasicStroke(2f));
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
            g2d.setColor(Color.BLACK);
            g2d.draw(covMort);

        }

        ImageIO.write(bi, "png", new File("mortality_AT.png"));

    }

    public static void drawRotate(Graphics2D g2d, double x, double y, int angle, String text, int fontSize) {

        Font font = new Font("Consolas", Font.BOLD, fontSize);
        g2d.setFont(font);

        g2d.translate((float) x, (float) y);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawString(text, 0, 0);
        g2d.rotate(-Math.toRadians(angle));
        g2d.translate(-(float) x, -(float) y);

    }

    protected static double[] toImageCoordinate(BufferedImage image, double instant, double y) {

        double xFrac = (instant - MIN_IMAGE_INSTANT) * 1.0 / (MAX_IMAGE_INSTANT - MIN_IMAGE_INSTANT);
        double yFrac = (y - MIN_IMAGE_Y) / (MAX_IMAGE_Y - MIN_IMAGE_Y);

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
