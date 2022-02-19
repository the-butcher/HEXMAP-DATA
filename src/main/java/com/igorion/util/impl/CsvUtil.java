package com.igorion.util.impl;

public class CsvUtil {

    private CsvUtil() {
        //no public instance
    }

    /**
     * remove any quotes that may be prefixing or postfixing the given string
     * @param quoted
     * @return
     */
    public static String removeQuotes(String quoted) {
        String unquoted = quoted;
        while (unquoted.startsWith("\"")) {
            unquoted = unquoted.substring(1);
        }
        while (unquoted.endsWith("\"")) {
            unquoted = unquoted.substring(0, unquoted.length() - 1);
        }
        return unquoted;
    }

    /**
     * adds quotes to the given value (any pre-exiting quotes are removed before (re)-adding quotes<br>
     * @param raw
     * @return the quoted string having exactly one quote on start and end of the string
     */
    public static String addQuotes(String raw) {
        return "\"" + removeQuotes(raw) + "\"";
    }

}
