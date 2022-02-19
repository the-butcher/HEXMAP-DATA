package com.igorion.report.dataset;

import java.util.Optional;

public interface IDataEntry<X, Y> {

    Y getKey();

    /**
     * get a named raw-value of a given type from the report-fragment
     * @param <T>
     * @param name
     * @param type
     * @return
     */
    <T> Optional<T> optValue(X key, IFieldType<T> type);

}
