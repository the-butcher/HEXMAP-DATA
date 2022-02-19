package com.igorion.report.value;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.igorion.report.dataset.IFieldType;
import com.igorion.util.impl.CsvUtil;

class FieldTypeImplString implements IFieldType<String> {

    @Override
    public Optional<String> optValue(Object rawValue) {
        if (rawValue != null) {
            String rawStringValue = String.valueOf(rawValue);
            if (StringUtils.isNotBlank(rawStringValue)) {
                return Optional.of(CsvUtil.removeQuotes(rawStringValue));
            }
        }
        return Optional.empty();
        //return Optional.ofNullable(CsvUtil.removeQuotes(String.valueOf(rawValue)));
    }

}
