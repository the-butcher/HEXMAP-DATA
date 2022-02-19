package com.igorion.logs.impl;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.RollingFileAppender;

/**
 * Log file appender, which we are using in our web application.
 */
public class RootAppender extends RollingFileAppender {

    private static final String PATTERN = "%n#%d{ISO8601} %-5p [%-32t]" + "%-8r %-16x %n%m%n";
    private static final String HEADER = String
                .format("TIMESTAMP               | LEVEL | THREAD%n-----------------------------------------------------------------------------------------------------------------------%n");

    private static final String MAX_FILE_SIZE = "10MB";
    private static final int MAX_BACKUP_INDEX = 9;

    private final File file;

    /**
     * @param pattern http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html
     */
    public RootAppender(File file) throws IOException {

        super(new LogLayout(PATTERN, HEADER), file.getAbsolutePath());

        this.file = file;

        setMaxFileSize(MAX_FILE_SIZE);
        setMaxBackupIndex(MAX_BACKUP_INDEX);
        setImmediateFlush(true);

        setEncoding("UTF-8");
        activateOptions();

    }

    @Override
    public void writeHeader() {
        if (this.file != null && this.file.length() == 0L)
            super.writeHeader();
    }

}
