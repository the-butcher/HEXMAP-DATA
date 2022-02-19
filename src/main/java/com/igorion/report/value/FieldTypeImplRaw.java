package com.igorion.report.value;

import java.util.Optional;

import com.igorion.report.dataset.IFieldType;

class FieldTypeImplRaw implements IFieldType<Object> {

    @Override
    public Optional<Object> optValue(Object rawValue) {
        return Optional.ofNullable(rawValue);
    }

}
