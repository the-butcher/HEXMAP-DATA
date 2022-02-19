package com.igorion.report.value;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.igorion.fail.EFailureCode;
import com.igorion.fail.impl.C19Failure;
import com.igorion.report.dataset.IFieldType;
import com.igorion.util.impl.CsvUtil;

public class FieldTypeImplDate implements IFieldType<Date> {

    private final SimpleDateFormat dateFormat;
    private final Pattern pattern;

    public FieldTypeImplDate(SimpleDateFormat dateFormat, String pattern) {
        this.dateFormat = dateFormat;
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public Optional<Date> optValue(Object rawValue) {
        try {
            String matchableStringValue = CsvUtil.removeQuotes(String.valueOf(rawValue));
            Matcher rawValueMatcher = this.pattern.matcher(matchableStringValue);
            if (rawValueMatcher.matches()) {
                return Optional.of(this.dateFormat.parse(matchableStringValue));
            } else {
                throw new C19Failure(EFailureCode.ILLEGAL_VALUE, "failed to parse date value from raw value (" + rawValue + ") because it could not be validated by regular expression prior to parsing", null);
            }
        } catch (Exception ex) {
            throw new C19Failure(EFailureCode.ILLEGAL_VALUE, "failed to parse date value from raw value (" + rawValue + ", format: " + this.dateFormat.toPattern() + ")", ex);
        }
    }

}
