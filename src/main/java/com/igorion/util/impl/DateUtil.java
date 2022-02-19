package com.igorion.util.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    public static final SimpleDateFormat DATE_FORMAT_UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    static {
        DATE_FORMAT_UTC.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static final SimpleDateFormat DATE_FORMAT_YEAR_ONLY = new SimpleDateFormat("yyyy");

    public static final long MILLISECONDS_OFF_2020 = 1577836800000L; // Date and time (GMT): Wednesday, January 1, 2020 0:00:00
    public static final long MILLISECONDS_OFF_2000 = 946684800000L; // Date and time (GMT): Saturday, 1. January 2000 00:00:00

    public static final long MILLISECONDS_PER_MINUTE = 60 * 1000;
    public static final long MILLISECONDS_PER_HOUR = 60 * 60 * 1000;
    public static final long MILLISECONDS_PER__DAY = 24 * MILLISECONDS_PER_HOUR;

    private DateUtil() {
        //no public instance
    }

    public static Date toMidnightUTC(Date date) {
        return new Date(date.getTime() - date.getTime() % MILLISECONDS_PER__DAY);
    }

    public static String toUtcString(Date date) {
        return DATE_FORMAT_UTC.format(date);
    }

    public static Date incrementByOneDay(Date previous) {
        return new Date(previous.getTime() + MILLISECONDS_PER__DAY);
    }

    public static int toDays2000(Date date) {
        return (int) ((date.getTime() - MILLISECONDS_OFF_2000) / MILLISECONDS_PER__DAY);
    }

    public static int toHours2020(Date date) {
        return (int) ((date.getTime() - MILLISECONDS_OFF_2020) / MILLISECONDS_PER_HOUR);
    }

    public static Date fromHours(int hours) {
        return new Date(hours * MILLISECONDS_PER_HOUR + MILLISECONDS_OFF_2020);
    }

    public static Date fromYearAndMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.ZONE_OFFSET, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

//    public static int getLastDayOfMonth(Date date) {
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//
//    }

}
