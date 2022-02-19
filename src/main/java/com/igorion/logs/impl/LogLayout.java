package com.igorion.logs.impl;

import org.apache.log4j.PatternLayout;

/**
 * Layout of a log file.
 */
public class LogLayout extends PatternLayout {

    private String header;

    /**
     * @param pattern http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html
     */
    public LogLayout(final String pattern, final String header) {
        super();

        setConversionPattern(pattern);
        this.header = header;
    }

    @Override
    public String getHeader() {
        return this.header;
    }

}
