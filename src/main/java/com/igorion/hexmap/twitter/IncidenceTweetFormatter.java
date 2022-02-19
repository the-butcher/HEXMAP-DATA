package com.igorion.hexmap.twitter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.igorion.hexmap.IIncidenceTweetFormatter;
import com.igorion.type.json.impl.JsonTypeImplHexmapDataRoot;

public class IncidenceTweetFormatter implements IIncidenceTweetFormatter {

    public static Map<String, String> TWITTER_MONO_MAP = new LinkedHashMap<>();
    static {
        TWITTER_MONO_MAP.put("0", "𝟎");
        TWITTER_MONO_MAP.put("1", "𝟏");
        TWITTER_MONO_MAP.put("2", "𝟐");
        TWITTER_MONO_MAP.put("3", "𝟑");
        TWITTER_MONO_MAP.put("4", "𝟒");
        TWITTER_MONO_MAP.put("5", "𝟓");
        TWITTER_MONO_MAP.put("6", "𝟔");
        TWITTER_MONO_MAP.put("7", "𝟕");
        TWITTER_MONO_MAP.put("8", "𝟖");
        TWITTER_MONO_MAP.put("9", "𝟗");
        TWITTER_MONO_MAP.put(".", ".");
        TWITTER_MONO_MAP.put(",", ",");
        TWITTER_MONO_MAP.put(":", ":");
        TWITTER_MONO_MAP.put(" ", " ");
    }

    private final String key;
    private final String title;

    public IncidenceTweetFormatter(String key, String title) {
        this.key = key;
        this.title = title;
    }

    @Override
    public void format(JsonTypeImplHexmapDataRoot dataRoot) {

        List<String> dateKeys = new ArrayList<>(dataRoot.getDateKeys());

        String dateKey22 = dateKeys.get(dateKeys.size() - 23);
        String dateKey21 = dateKeys.get(dateKeys.size() - 22);
        String dateKey15 = dateKeys.get(dateKeys.size() - 16);
        String dateKey14 = dateKeys.get(dateKeys.size() - 15);
        String dateKey08 = dateKeys.get(dateKeys.size() - 9);
        String dateKey07 = dateKeys.get(dateKeys.size() - 8);
        String dateKey01 = dateKeys.get(dateKeys.size() - 2);
        String dateKey00 = dateKeys.get(dateKeys.size() - 1);

        double value22 = dataRoot.getValue(dateKey22, this.key, 0);
        double value21 = dataRoot.getValue(dateKey21, this.key, 0);
        double value15 = dataRoot.getValue(dateKey15, this.key, 0);
        double value14 = dataRoot.getValue(dateKey14, this.key, 0);
        double value08 = dataRoot.getValue(dateKey08, this.key, 0);
        double value07 = dataRoot.getValue(dateKey07, this.key, 0);
        double value01 = dataRoot.getValue(dateKey01, this.key, 0);
        double value00 = dataRoot.getValue(dateKey00, this.key, 0);

        int cases21 = (int) (value21 - value22);
        int cases14 = (int) (value14 - value15);
        int cases07 = (int) (value07 - value08);
        int cases00 = (int) (value00 - value01);

        System.out.println("-".repeat(100));
        System.out.println(this.title);
        System.out.println(toDateValueLine(dateKey00, cases00, cases07));
        System.out.println(toDateValueLine(dateKey07, cases07, cases14));
        System.out.println(toDateValueLine(dateKey14, cases14, cases21));
        System.out.println("Quelle: https://data.gv.at/covid-19/");
        System.out.println("#covid19at #omicronat");
        System.out.println("-".repeat(100));

    }

    protected static String toDateValueLine(String date, int value, int previousValue) {
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
        String twitterMono = toTwitterMono(String.format("%s: %5s", date, nf.format(value)));
        return twitterMono + getChangeIndicator(value, previousValue);
    }

    protected static String toTwitterMono(String value) {
        StringBuilder twitterMonoBuilder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            twitterMonoBuilder.append(TWITTER_MONO_MAP.get(value.substring(i, i + 1)));
        }
        return twitterMonoBuilder.toString();
    }

    protected static String getChangeIndicator(int valueB, int valueA) {
        double ratio = valueA * 1.0 / valueB - 1;
        if (Math.abs(ratio) < 0.05) {
            return " (→)";
        } else if (ratio < 0) {
            return " (↑)";
        } else {
            return " (↓)";
        }
    }

}
