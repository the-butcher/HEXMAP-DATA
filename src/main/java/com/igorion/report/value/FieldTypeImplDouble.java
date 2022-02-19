package com.igorion.report.value;

import java.util.Optional;

import com.igorion.fail.EFailureCode;
import com.igorion.fail.impl.C19Failure;
import com.igorion.report.dataset.IFieldType;
import com.igorion.util.impl.CsvUtil;

class FieldTypeImplDouble implements IFieldType<Double> {

    @Override
    public Optional<Double> optValue(Object rawValue) {
        try {
            String rawStringValue = CsvUtil.removeQuotes(String.valueOf(rawValue));
            return Optional.ofNullable(Double.parseDouble(rawStringValue));
        } catch (Exception ex) {
            throw new C19Failure(EFailureCode.ILLEGAL_VALUE, "failed to parse double value", ex);
        }
    }

}
