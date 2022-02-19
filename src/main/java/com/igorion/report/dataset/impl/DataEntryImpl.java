package com.igorion.report.dataset.impl;

import java.util.Map;
import java.util.Optional;

import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IFieldType;

public class DataEntryImpl<X, Y> implements IDataEntry<X, Y> {

    private final Y keyY;
    private final Map<X, Object> values;

    public DataEntryImpl(Y key, Map<X, Object> values) {
        this.keyY = key;
        this.values = values;
    }

    @Override
    public Y getKey() {
        return this.keyY;
    }

    @Override
    public <T> Optional<T> optValue(X keyX, IFieldType<T> type) {
        return DataEntryImpl.optValue(keyX, type, this.values);
    }

    public static <T, X> Optional<T> optValue(X keyX, IFieldType<T> type, Map<X, Object> attributes) {
        if (attributes.containsKey(keyX)) {
            return type.optValue(attributes.get(keyX));
        } else {
            return Optional.empty();
        }
    }

}
