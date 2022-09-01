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

import javax.imageio.ImageIO;

import com.igorion.hexmap.mortality.impl.Mortality;
import com.igorion.hexmap.mortality.impl.NutsRegions;
import com.igorion.util.impl.DateUtil;
import com.igorion.util.impl.Statistics;

public class MortalityParserNuts1 {

    static final Color COLOR_AVERAGE_MORTALITY = Color.GRAY;

    static final Date POP_DATE_A = new Date(2020 - 1900, 0, 1);
    // static final Date POP_DATE_B = new Date(2022 - 1900, 0, 1);
    static final Date POP_DATE_B = new Date(2022 - 1900, 3, 10);
    static final Date POP_DATE_C = new Date(2022 - 1900, 4, 1);

    static final long MIN_IMAGE_INSTANT = POP_DATE_A.getTime() - DateUtil.MILLISECONDS_PER__DAY * 48;
    static final long MAX_IMAGE_INSTANT = POP_DATE_C.getTime() + DateUtil.MILLISECONDS_PER__DAY * 6;

    static final double MIN_IMAGE_Y = -5.5;
    static final double MAX_IMAGE_Y = 500.5;

    public static final SimpleDateFormat DATE_FORMAT_MMYY = new SimpleDateFormat("MM.yyyy");

    // Denmark https://www.statistikbanken.dk/statbank5a/SelectVarVal/Define.asp?Maintable=DODC2&PLanguage=1

    public static final File FOLDER_________BASE = new File("C:\\privat\\_projects_cov\\covid2019_hexmap_data\\mortality");
    // public static final File FILE_NUTS1______GKZ = new File(FOLDER_________BASE, "nuts1_gkz.csv");
    public static final File FILE_NUTS1_AGE_WEEK = new File(FOLDER_________BASE, "nuts1_age_week_02.csv");
//    public static final File FILE_NUTS1_AGE_WEEK = new File(FOLDER_________BASE, "nuts1_week.csv");
    public static final File FILE_GKZ_POPULATION = new File(FOLDER_________BASE, "population_nuts1_00n90.csv");

    public static void main(String[] args) throws Exception {

//        String iso2Digit = "DK";
//        String country = "Dänemark";

        String iso2Digit = "AT";
        String country = "Österreich";

//        String iso2Digit = "SE";
//        String country = "Schweden";

        INutsRegion nutsRegion = NutsRegions.getRegions(FILE_GKZ_POPULATION, EAgeGroup.values()).get(iso2Digit);
        System.out.println(nutsRegion.getNuts() + " / " + nutsRegion.getPopulation(EAgeGroup.ETOTAL, POP_DATE_C));

//        double population = nutsRegion.getPopulation(EAgeGroup.ETOTAL, POP_DATE_B);
//        double weeklyDeaths = 0;
//        double extraMortality = weeklyDeaths * 100000 / population;
//        System.out.println("extraMortality: " + extraMortality);

        BufferedImage bi = new BufferedImage(1200, 675, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        IMortality mortality = Mortality.optMortalityByNuts3(nutsRegion.getNuts(), FILE_NUTS1_AGE_WEEK).orElseThrow();

        // EAgeGroup ageGroup = EAgeGroup.values()[i];
        double[] imageCoordinate;

        EAgeGroup[] drawGroups = new EAgeGroup[] {
                    EAgeGroup.E85_89
                    // EAgeGroup.E90_00,y
                    // EAgeGroup.E10_14
        };
        // for (int i = 0 /* EAgeGroup.values().length - 1 */; i >= 0; i--) {
        for (EAgeGroup ageGroup : drawGroups) {

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

//            GeneralPath covPlus = new GeneralPath();
//            int countCovPlus = 0;

            for (long instant = POP_DATE_A.getTime(); instant <= POP_DATE_C.getTime(); instant += DateUtil.MILLISECONDS_PER__DAY) {

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

                imageCoordinate = toImageCoordinate(bi, instant, statsRel.getAverage() + statsRel.getStandardDeviation() * 2);
                if (countCiUpper95 == 0) {
                    ciUpper95.moveTo(imageCoordinate[0], bi.getHeight());
                }
                ciUpper95.lineTo(imageCoordinate[0], imageCoordinate[1]);
                countCiUpper95++;

                imageCoordinate = toImageCoordinate(bi, instant, statsRel.getAverage() - statsRel.getStandardDeviation() * 2);
                if (countCiLower95 == 0) {
                    ciLower95.moveTo(imageCoordinate[0], bi.getHeight());
                }
                ciLower95.lineTo(imageCoordinate[0], imageCoordinate[1]);
                countCiLower95++;

                imageCoordinate = toImageCoordinate(bi, instant, statsRel.getAverage() + statsRel.getStandardDeviation());
                if (countCiUpper68 == 0) {
                    ciUpper68.moveTo(imageCoordinate[0], bi.getHeight());
                }
                ciUpper68.lineTo(imageCoordinate[0], imageCoordinate[1]);
                countCiUpper68++;

                imageCoordinate = toImageCoordinate(bi, instant, statsRel.getAverage() - statsRel.getStandardDeviation());
                if (countCiLower68 == 0) {
                    ciLower68.moveTo(imageCoordinate[0], bi.getHeight());
                }
                ciLower68.lineTo(imageCoordinate[0], imageCoordinate[1]);
                countCiLower68++;

                double[] imageCoordinateAvgMort = toImageCoordinate(bi, instant, statsRel.getAverage());
                if (countAvgMort == 0) {
                    avgMort.moveTo(imageCoordinateAvgMort[0], imageCoordinateAvgMort[1]);
                }
                avgMort.lineTo(imageCoordinateAvgMort[0], imageCoordinateAvgMort[1]);
                countAvgMort++;

                if (instant <= POP_DATE_B.getTime()) {

                    double mortalityVal = mortality.getWeeklyMortality(nutsRegion, ageGroup, tempDate);

                    System.out.println(String.format("tempDate: %s >> %6.4f >> %6.4f", tempDate, mortalityVal, statsAbs.getAverage()));

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

        }

        for (double y = 0; y <= 35; y += 5) {

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

                drawRotate(g2d, imageCoordinate[0] + 4, imageCoordinate[1] + 53, -90, DATE_FORMAT_MMYY.format(date), 13);

            }
        }

        drawRotate(g2d, 550, 665, 0, "Datum", 17);
        drawRotate(g2d, 23, 480, -90, "Sterblichkeit / Woche / 100.000", 17);

        drawRotate(g2d, 890, 23, 0, "@FleischerHannes https://ec.europa.eu/eurostat", 13); // https://ec.europa.eu/eurostat/ // https://www.statistik.at
        drawRotate(g2d, 550, 23, 0, country, 17); //  (2010 - 2019)

        drawLegend(g2d, 740, 665, Color.BLACK, "Sterblichkeit");
        drawLegend(g2d, 890, 665, COLOR_AVERAGE_MORTALITY, "Sterblichkeit 2000-2019 / CI68 / CI95");

        ImageIO.write(bi, "png", new File("mortality_" + iso2Digit + ".png"));

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
