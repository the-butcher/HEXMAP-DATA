package com.igorion.report.dataset.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.igorion.fail.EFailureCode;
import com.igorion.fail.impl.C19Failure;
import com.igorion.logs.ELogger;
import com.igorion.report.dataset.IDataEntry;
import com.igorion.report.dataset.IDataSet;
import com.igorion.report.dataset.IDataSetFactory;

public class DataSetFactoryImplXlsx implements IDataSetFactory<String, Long> {

    private final String sheetName;
    private final Function<XSSFSheet, Integer> minColResolver;
    private final Function<XSSFSheet, Integer> minRowResolver;
    private final Function<XSSFSheet, Optional<String>> minCellValueSupplier;

    public DataSetFactoryImplXlsx(String sheetName, Function<XSSFSheet, Integer> minColResolver, Function<XSSFSheet, Integer> minRowResolver, Function<XSSFSheet, Optional<String>> minCellValueSupplier) {
        this.sheetName = sheetName;
        this.minColResolver = minColResolver;
        this.minRowResolver = minRowResolver;
        this.minCellValueSupplier = minCellValueSupplier;
    }

    @Override
    public IDataSet<String, Long> createDataSet(BufferedReader csvReader, Predicate<String> linePredicate) {
        return null;
    }

    @Override
    public IDataSet<String, Long> createDataSet(BufferedReader reader) {
        throw new RuntimeException("not implemented (parse xlsx from reader), use create data set from stream instead");
    }

    @Override
    public IDataSet<String, Long> createDataSet(InputStream input, Charset charset) {

        try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook(input)) {

            XSSFSheet xssfSheet = xssfWorkbook.getSheet(this.sheetName);
            if (xssfSheet != null) {

                Map<Integer, String> headers = new LinkedHashMap<>();

                Optional<String> oMinCellValue = this.minCellValueSupplier.apply(xssfSheet);

                int minRow = this.minRowResolver.apply(xssfSheet);
                int maxRow = xssfSheet.getPhysicalNumberOfRows();
                int minCol = this.minColResolver.apply(xssfSheet);

                XSSFRow headerRow = xssfSheet.getRow(minRow);
                int maxCol = headerRow.getPhysicalNumberOfCells(); //regarding physical number of cells, this is a "hard" limit for headers and needs to be taken "as is"
                for (int cellnum = minCol; cellnum < maxCol; cellnum++) {

                    XSSFCell xssfCell = headerRow.getCell(cellnum);
                    if (cellnum == minCol && oMinCellValue.isPresent()) {
                        xssfCell.setCellValue(oMinCellValue.get());
                    }

                    if (xssfCell != null) {
                        if (xssfCell.getCellType() == CellType.NUMERIC) {
                            headers.put(cellnum, String.valueOf(xssfCell.getNumericCellValue()));
                        } else {
                            headers.put(cellnum, xssfCell.getStringCellValue());
                        }
                    } else {
                        headers.put(cellnum, "NULL");
                    }

                }

                List<IDataEntry<String, Long>> dataEntries = new ArrayList<>();
                long key = 0L;
                for (int rownum = minRow + 1; rownum < maxRow; rownum++) {
                    Map<String, Object> values = new HashMap<>();
                    XSSFRow xssfRow = xssfSheet.getRow(rownum);
                    if (xssfRow != null) {
                        for (int cellnum = minCol; cellnum < maxCol; cellnum++) {
                            XSSFCell xssfCell = xssfRow.getCell(cellnum);
                            if (xssfCell != null) {
                                if (xssfCell.getCellType() == CellType.NUMERIC || xssfCell.getCellType() == CellType.FORMULA) {
                                    values.put(headers.get(cellnum), xssfCell.getNumericCellValue());
                                } else {
                                    String stringCellValue = xssfCell.getStringCellValue();
                                    if (StringUtils.isNotBlank(stringCellValue)) {
                                        values.put(headers.get(cellnum), stringCellValue);
                                    }
                                }
                            }
                        }
                    }
                    if (!values.isEmpty()) {
                        dataEntries.add(new DataEntryImpl<>(key++, values));
                    } else {
                        ELogger.PARSE.warn(new C19Failure(EFailureCode.COLLECT_FAILURE,
                                    "failed to parse row (sheet: " + this.sheetName + ", rownum: " + rownum + ", maxRow: " + maxRow + ") because there were only null values in that row", null));
                    }
                }

                List<String> headerList = new ArrayList<>(headers.values());
                return new DataSetImpl<>(headerList, dataEntries);

            } else {
                throw new C19Failure(EFailureCode.COLLECT_FAILURE, "failed to find sheet (" + this.sheetName + ")", null);
            }

        } catch (Exception ex) {
            throw new C19Failure(EFailureCode.COLLECT_FAILURE, "failed to create xlsx dataset", ex);
        }

    }

}
