package com.igorion.report.dataset.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.igorion.fail.EFailureCode;
import com.igorion.fail.impl.C19Failure;
import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IDataSet;
import com.igorion.report.dataset.IDataSetFactory;
import com.igorion.util.impl.CsvUtil;

public class DataSetFactoryImplCsv implements IDataSetFactory<String, Long> {

    public static final String BOM_UTF_8 = "ï»¿";
    //public static final String CSV_SPLITTER = "(,|;)(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    private String templateHeaderLine;
    private final String splitterH;
    private final String splitterV;

    public DataSetFactoryImplCsv() {
        this.templateHeaderLine = null;
        this.splitterH = "(,|;)";
        this.splitterV = "(,|;)(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    }

    public DataSetFactoryImplCsv(String splitterH) {
        this.templateHeaderLine = null;
        this.splitterH = splitterH;
        this.splitterV = splitterH + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    }

    public DataSetFactoryImplCsv(String templateHeaderLine, String splitterH) {
        this.templateHeaderLine = templateHeaderLine;
        this.splitterH = splitterH;
        this.splitterV = splitterH + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    }

    @Override
    public IDataSet<String, Long> createDataSet(InputStream input, Charset charset) {
        return createDataSet(new BufferedReader(new InputStreamReader(input, charset)));
    }

    @Override
    public IDataSet<String, Long> createDataSet(BufferedReader reader) {
        return createDataSet(reader, line -> true);
    }

    @Override
    public IDataSet<String, Long> createDataSet(BufferedReader csvReader, Predicate<String> linePredicate) {

        try {

            String headerLine = null;
            if (StringUtils.isBlank(this.templateHeaderLine)) {
                headerLine = csvReader.readLine().trim();
                while (headerLine.contains(BOM_UTF_8)) {
                    headerLine = headerLine.replace(BOM_UTF_8, "");
                }
                while (headerLine.charAt(0) == 65279) {
                    headerLine = headerLine.substring(1);
                }
            } else {
                headerLine = this.templateHeaderLine;
            }
            String[] headersRaw = headerLine.trim().split(this.splitterH);
            List<String> keysY = Arrays.stream(headersRaw).map(String::trim).map(CsvUtil::removeQuotes).map(String::trim).collect(Collectors.toList());

            String valueLine;
            long entryIndex = 0L;
            List<IDataEntry<String, Long>> dataEntries = new ArrayList<>();
            while (StringUtils.isNotBlank(valueLine = csvReader.readLine())) {
                if (linePredicate.test(valueLine)) {
                    valueLine = valueLine.trim() + ";END_OF_LINE"; //add a list entry to be sure empty slots get initialized
                    String[] valuesRaw = valueLine.split(this.splitterV);
                    Map<String, Object> values = new HashMap<>();
                    for (int i = 0; i < keysY.size(); i++) {
                        values.putIfAbsent(keysY.get(i), valuesRaw[i]);
                    }
                    dataEntries.add(new DataEntryImpl<>(entryIndex, values));
                }
                entryIndex++;
//                if (entryIndex % 100000 == 0) {
//                    System.out.println("loading: " + entryIndex);
//                }
            }

            return new DataSetImpl<>(keysY, dataEntries);

        } catch (Exception ex) {
            throw new C19Failure(EFailureCode.COLLECT_FAILURE, "failed to create csv dataset", ex);
        }

    }

}
