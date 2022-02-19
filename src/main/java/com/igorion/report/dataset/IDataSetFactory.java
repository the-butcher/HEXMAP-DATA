package com.igorion.report.dataset;

import java.io.BufferedReader;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.function.Predicate;

import com.igorion.type.live.ILiveType;

public interface IDataSetFactory<X, Y> extends ILiveType {

    IDataSet<X, Y> createDataSet(BufferedReader reader, Predicate<String> linePredicate);

    IDataSet<X, Y> createDataSet(BufferedReader reader);

    IDataSet<X, Y> createDataSet(InputStream input, Charset charset);

}
