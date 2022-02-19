package com.igorion.report.value;

import com.igorion.report.dataset.IFieldType;

public class FieldTypes {

    public static final IFieldType<Object> RAW = new FieldTypeImplRaw();
    public static final IFieldType<String> STRING = new FieldTypeImplString();
    public static final IFieldType<Long> LONG = new FieldTypeImplLong();
    public static final IFieldType<Double> DOUBLE = new FieldTypeImplDouble();

    private FieldTypes() {
        //no public instance
    }

}
