package com.igorion.report.dataset;

import java.util.Optional;

public interface IFieldType<T> {

    Optional<T> optValue(Object rawValue);

}
