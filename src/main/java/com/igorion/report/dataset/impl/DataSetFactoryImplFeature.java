package com.igorion.report.dataset.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igorion.fail.EFailureCode;
import com.igorion.fail.impl.C19Failure;
import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IDataSet;
import com.igorion.report.dataset.IDataSetFactory;
import com.igorion.report.value.FieldTypes;
import com.igorion.type.json.response.JsonTypeImplFeaturePolygon;
import com.igorion.type.json.response.JsonTypeImplField;
import com.igorion.type.json.response.JsonTypeImplQueryResultPolygon;

public class DataSetFactoryImplFeature implements IDataSetFactory<String, Long> {

    @Override
    public IDataSet<String, Long> createDataSet(InputStream input, Charset charset) {
        return createDataSet(new BufferedReader(new InputStreamReader(input, charset)));
    }

    @Override
    public IDataSet<String, Long> createDataSet(BufferedReader csvReader, Predicate<String> linePredicate) {
        return null;
    }

    @Override
    public IDataSet<String, Long> createDataSet(BufferedReader reader) {

        try {

            JsonTypeImplQueryResultPolygon queryResultJo = new ObjectMapper().readValue(reader, JsonTypeImplQueryResultPolygon.class);

            List<String> keysY = queryResultJo.getFields().stream().map(JsonTypeImplField::getName).collect(Collectors.toList());
            List<IDataEntry<String, Long>> dataEntries = new ArrayList<>();
            long counter = 0;
            for (JsonTypeImplFeaturePolygon featureJo : queryResultJo.getFeatures()) {
                long syntheticOid = counter++;
                long oid = DataEntryImpl.optValue(queryResultJo.getObjectIdFieldName(), FieldTypes.LONG, featureJo.getAttributes())
                            .orElseGet(() -> syntheticOid);
                dataEntries.add(new DataEntryImpl<>(oid, featureJo.getAttributes()));
            }
            return new DataSetImpl<>(keysY, dataEntries);

        } catch (Exception ex) {
            throw new C19Failure(EFailureCode.COLLECT_FAILURE, "failed to create csv dataset", ex);
        }

    }

}
