package com.igorion.report.dataset.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IDataSet;
import com.igorion.report.value.FieldTypes;

public class DataSetImpl<X, Y> implements IDataSet<X, Y> {

    private final List<X> keysX;
    private final List<IDataEntry<X, Y>> dataEntries;

    public DataSetImpl(List<X> keysX, List<? extends IDataEntry<X, Y>> dataEntries) {
        this.keysX = keysX;
        this.dataEntries = new ArrayList<>(dataEntries);
    }

    @Override
    public List<IDataEntry<X, Y>> getEntriesY() {
        return Collections.unmodifiableList(this.dataEntries);
    }

    @Override
    public List<X> getKeysX() {
        return this.keysX;
    }

    @Override
    public Optional<IDataEntry<Y, X>> optEntryX(X keyX) {
        Map<Y, Object> values = new HashMap<>();
        for (IDataEntry<X, Y> dataEntry : this.dataEntries) {
            values.put(dataEntry.getKey(), dataEntry.optValue(keyX, FieldTypes.RAW).orElseGet(() -> null));
        }
        return Optional.of(new DataEntryImpl<>(keyX, values));
    }

    @Override
    public List<Y> getKeysY() {
        return this.dataEntries.stream().map(IDataEntry::getKey).collect(Collectors.toList());
    }

    @Override
    public Optional<IDataEntry<X, Y>> optEntryY(Y keyY) {
        return this.dataEntries.stream().filter(entry -> entry.getKey().equals(keyY)).findFirst();
    }

}
