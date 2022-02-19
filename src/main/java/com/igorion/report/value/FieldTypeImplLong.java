package com.igorion.report.value;

import java.util.Optional;

import com.igorion.fail.EFailureCode;
import com.igorion.fail.impl.C19Failure;
import com.igorion.report.dataset.IFieldType;
import com.igorion.util.impl.CsvUtil;

class FieldTypeImplLong implements IFieldType<Long> {

    @Override
    public Optional<Long> optValue(Object rawValue) {
        try {
            String rawStringValue = CsvUtil.removeQuotes(String.valueOf(rawValue));
            //if (StringUtils.isNumeric(rawStringValue)) {
            return Optional.ofNullable(Double.valueOf(rawStringValue).longValue());
            //} else {
            //    throw new C19Failure(EFailureCode.ILLEGAL_VALUE, "value is not numeric (" + rawValue + ")", null);
            //}
        } catch (Exception ex) {
            throw new C19Failure(EFailureCode.ILLEGAL_VALUE, "failed to parse long value (" + rawValue + ")", ex);
        }
    }

}
